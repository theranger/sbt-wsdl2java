/*
 * Copyright 2016 Baltnet Communications LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ee.risk.sbt.plugins.wsdl2java.plugin

import java.net.MalformedURLException
import javax.net.ssl.{HttpsURLConnection, SSLHandshakeException}

import ee.risk.sbt.plugins.wsdl2java.ssl.{SSLClient, SSLTrustManagerFactory, TrustStore}
import ee.risk.sbt.plugins.wsdl2java.wsdl.{JAXWSClient, WSDLParser}
import sbt._
import sbt.plugins.JvmPlugin

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-14
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
object Wsdl2Java extends AutoPlugin with WSDLParser {
	override def requires = JvmPlugin
	override def trigger = allRequirements
	override val projectSettings = inConfig(Compile)(Settings.defaults(this))
	val autoImport = Settings.autoImport

	override def parseWSDL(log: Logger, rootDir: String, paths: Map[String, String], trustStore: File, bindings: Seq[String]) = {
		val localTrustStore = new TrustStore(log)
		localTrustStore.load(trustStore)

		val defaultTrustStore = new TrustStore(log)
		defaultTrustStore.load()

		val jaxwsClient = new JAXWSClient(log)
		var bindingArgs = Seq[String]()

		for (binding <- bindings) {
			bindingArgs ++= Seq("-b", binding)
		}

		for ((src, dst) <- paths) {
			try {
				val url = new URL(src)
				log.info("Parsing WSDL from " + src)
				val sslClient = createSSLClient(log, localTrustStore, defaultTrustStore)
				HttpsURLConnection.setDefaultSSLSocketFactory(sslClient.getSSLContext.getSocketFactory)
				jaxwsClient.generateWSDL(url, new File(rootDir), Seq("-Xnocompile", "-s", rootDir, "-p", dst) ++ bindingArgs)
			}
			catch {
				case _: MalformedURLException => log.warn("Ignoring non-URL path " + src)
			}
		}
	}

	private def createSSLClient(log: Logger, localTrustStore: TrustStore, defaultTrustStore: TrustStore): SSLClient = {
		// Initialize local trust manager
		val sslTrustManagerFactory = new SSLTrustManagerFactory(log, localTrustStore, defaultTrustStore)
		new SSLClient(log, sslTrustManagerFactory.getTrustManagers)
	}

	override def queryCertificates(log: Logger, paths: Map[String, String], trustStore: File) = {
		val localTrustStore = new TrustStore(log)
		localTrustStore.load(trustStore)

		val defaultTrustStore = new TrustStore(log)
		defaultTrustStore.load()

		for ((src, dst) <- paths) {
			try {
				val url = new URL(src)
				log.info("Looking up certificates for " + src)
				createSSLClient(log, localTrustStore, defaultTrustStore).queryCertificates(url)
			}
			catch {
				case _: SSLHandshakeException => localTrustStore.save(trustStore)
				case _: MalformedURLException => log.warn("Ignoring non-URL path " + src)
			}
		}
	}
}
