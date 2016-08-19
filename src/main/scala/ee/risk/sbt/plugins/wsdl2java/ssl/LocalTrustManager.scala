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
import java.security.cert.{CertificateException, X509Certificate}
import javax.net.ssl.{TrustManagerFactory, X509TrustManager}

import sbt._

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-15
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
class LocalTrustManager(log: Logger, trustStore: TrustStore) extends X509TrustManager {
	private val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
	trustManagerFactory.init(trustStore.getKeyStore)
	private val isInitialized = trustStore.getKeyStore.size() > 0

	private def getTrustManager: X509TrustManager = trustManagerFactory.getTrustManagers.head.asInstanceOf[X509TrustManager]

	override def getAcceptedIssuers: Array[X509Certificate] = {
		getTrustManager.getAcceptedIssuers
	}

	override def checkClientTrusted(chain: Array[X509Certificate], authType: String) = {
		getTrustManager.getAcceptedIssuers
	}

	override def checkServerTrusted(chain: Array[X509Certificate], authType: String) = {
		try {
			getTrustManager.checkServerTrusted(chain, authType)
		}
		catch {
			case ex: RuntimeException => throw new CertificateException(ex.getMessage)
		}
		finally {
			for (certificate <- chain) {
				trustStore.addCertificate(certificate)
			}
		}
	}
}
