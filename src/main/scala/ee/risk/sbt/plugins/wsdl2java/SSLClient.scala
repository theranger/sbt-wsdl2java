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

package ee.risk.sbt.plugins.wsdl2java

import java.io.{File, FileInputStream, FileOutputStream}
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{SSLContext, SSLSocket, TrustManagerFactory}

import sbt.URL

/**
	* Created by The Ranger (ranger@risk.ee) on 2016-08-14
	* for Baltnet Communications LLC (info@baltnet.ee)
	*/
class SSLClient(directory: File) {
	def loadTrustStore(file: String): SSLContext = {
		val trustStore = KeyStore.getInstance(KeyStore.getDefaultType)
		val inputStream = new FileInputStream(new File(directory, file))

		trustStore.load(inputStream, "changeit".toCharArray)
		inputStream.close()

		val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
		trustManagerFactory.init(trustStore)

		val sslContext = SSLContext.getDefault
		sslContext.init(null, trustManagerFactory.getTrustManagers, new SecureRandom())

		sslContext
	}

	def queryCertificates(url: URL, file: String): Unit = {
		val sslSocketFactory = loadTrustStore(file).getSocketFactory
		val sslSocket = sslSocketFactory.createSocket(url.getHost, url.getPort).asInstanceOf[SSLSocket]
		sslSocket.startHandshake()
		sslSocket.close()

		val outputStream = new FileOutputStream(new File(directory, file))
		outputStream.close()
	}
}
