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

package ee.risk.sbt.plugins.wsdl2java.ssl
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.{TrustManagerFactory, X509TrustManager}

import sbt._

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-15
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
class SSLTrustManager(log: Logger, directory: File) extends X509TrustManager {
	private val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
	private var certificates = List[X509Certificate]()
	private val trustStore = KeyStore.getInstance(KeyStore.getDefaultType)

	def init(storeName: String): Unit = {
		val separator = java.io.File.separatorChar
		val sysDir = new File(System.getProperty("java.home") + separator + "lib" + separator + "security")

		var file = new File(directory, storeName)
		if (!file.isFile) file = new File(sysDir, "jssecacerts")
		if (!file.isFile) file = new File(sysDir, "cacerts")

		log.info("Using trust store " + file.getAbsolutePath)
		val inputStream = new FileInputStream(file)
		trustStore.load(inputStream, "changeit".toCharArray)
		inputStream.close()

		trustManagerFactory.init(trustStore)
	}

	def getCertificateChain: List[X509Certificate] = certificates
	def getTrustManager: X509TrustManager = trustManagerFactory.getTrustManagers.head.asInstanceOf[X509TrustManager]

	def loadCertificates(certificates: List[X509Certificate]): Unit = {
		if (certificates.isEmpty) return

		for (certificate <- certificates) {
			trustStore.setCertificateEntry(trustStore.size().toString, certificate)
		}

		trustManagerFactory.init(trustStore)
	}

	override def getAcceptedIssuers: Array[X509Certificate] = {
		getTrustManager.getAcceptedIssuers
	}

	override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {
		getTrustManager.getAcceptedIssuers
	}

	override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {
		certificates = chain.toList
		getTrustManager.checkServerTrusted(chain, authType)
	}
}
