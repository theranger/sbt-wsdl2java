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
import javax.net.ssl.X509TrustManager

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-19
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
class CompositeTrustManager(trustManagers: Seq[X509TrustManager]) extends X509TrustManager {
	override def getAcceptedIssuers: Array[X509Certificate] = {
		var certificates = Seq[X509Certificate]()

		for (trustManager <- trustManagers) {
			certificates ++= trustManager.getAcceptedIssuers
		}

		certificates.toArray
	}

	override def checkClientTrusted(x509Certificates: Array[X509Certificate], authType: String): Unit = {
		try {
			for (trustManager <- trustManagers) {
				trustManager.checkClientTrusted(x509Certificates, authType)
				return
			}
		}
		catch {
			case _: CertificateException =>
		}

		throw new CertificateException("None of the trust managers trust this certificate chain")
	}

	override def checkServerTrusted(x509Certificates: Array[X509Certificate], authType: String): Unit = {
		try {
			for (trustManager <- trustManagers) {
				trustManager.checkServerTrusted(x509Certificates, authType)
				return
			}
		}
		catch {
			case _: CertificateException =>
		}

		throw new CertificateException("None of the trust managers trust this certificate chain")
	}
}
