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

import java.io._
import java.security.KeyStore
import java.security.cert.X509Certificate

import sbt.Logger

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-19
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
class TrustStore(log: Logger) {
	private val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
	private val defaultPassword = "changeit"

	def getKeyStore = keyStore

	def load(): Unit = {
		val separator = java.io.File.separatorChar
		val sysDir = new File(System.getProperty("java.home") + separator + "lib" + separator + "security")

		var file = new File(sysDir, "jssecacerts")
		if (!file.isFile) file = new File(sysDir, "cacerts")
		load(file)
	}

	def load(file: File, password: String = defaultPassword): Unit = {
		try {
			val inputStream = new FileInputStream(file)
			log.info("Using trust store at " + file.getAbsolutePath)
			keyStore.load(inputStream, password.toCharArray)
			inputStream.close()
		}
		catch {
			case _: FileNotFoundException =>
				keyStore.load(null, password.toCharArray)
				log.warn("Could not load data from store " + file.getAbsolutePath + ", skipping")
		}
	}

	def save(file: File, password: String = defaultPassword): Unit = {
		try {
			log.info("Saving keystore to file " + file.getAbsolutePath)
			keyStore.store(new FileOutputStream(file), password.toCharArray)
		}
		catch {
			case ex: IOException => throw new SSLException("Could not save keystore to file " + ex.getMessage)
		}
	}

	def addCertificate(certificate: X509Certificate): Unit = {
		keyStore.setCertificateEntry("test", certificate)
	}
}
