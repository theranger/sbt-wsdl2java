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

import java.security.cert.X509Certificate
import javax.net.ssl._

import sbt.{Logger, URL}

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-14
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
class SSLClient(log: Logger, sslTrustManager: SSLTrustManager) {

	private val sslContext = SSLContext.getInstance("TLS")
	sslContext.init(null, Array[TrustManager] {sslTrustManager}, null)

	def getSSLContext: SSLContext = sslContext

	def queryCertificates(url: URL): List[X509Certificate] = {
		val sslSocketFactory = sslContext.getSocketFactory
		val sslSocket = sslSocketFactory.createSocket(url.getHost, url.getPort).asInstanceOf[SSLSocket]

		try {
			sslSocket.startHandshake()
			log.info("All certificates are trusted")
		}
		catch {
			case _: SSLException => log.info("Untrusted certificate found")
		}

		sslSocket.close()
		sslTrustManager.getCertificateChain
	}
}
