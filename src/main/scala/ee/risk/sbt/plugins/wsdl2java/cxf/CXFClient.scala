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

package ee.risk.sbt.plugins.wsdl2java.cxf

import java.io.InputStream
import javax.net.ssl.SSLContext

import org.apache.cxf.BusFactory
import org.apache.cxf.resource.{ResourceManager, ResourceResolver}
import org.apache.http.auth.AuthSchemeProvider
import org.apache.http.client.config.AuthSchemes
import org.apache.http.client.methods.HttpGet
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.auth._
import org.apache.http.impl.client.HttpClients
import sbt.{File, Logger, URL}

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-14
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
class CXFClient(log: Logger, directory: File, sslContext: SSLContext) extends ResourceResolver {
	private val bus = BusFactory.getDefaultBus()
	private val resourceManager = bus.getExtension(classOf[ResourceManager])
	resourceManager.addResourceResolver(this)

	protected override def resolve[T](resourceName: String, resourceType: Class[T]): T = {
		resourceType.asInstanceOf
	}

	protected override def getAsStream(path: String): InputStream = {
		if (!path.startsWith("https")) return null

		val sslSocketFactory = new SSLConnectionSocketFactory(sslContext)
		val authSchemeRegistry = RegistryBuilder.create[AuthSchemeProvider]()
			.register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
			.register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
			.register(AuthSchemes.NTLM, new NTLMSchemeFactory())
			.register(AuthSchemes.BASIC, new BasicSchemeFactory())
			.register(AuthSchemes.DIGEST, new DigestSchemeFactory())
			.build()

		val httpClient = HttpClients.custom()
			.setSSLSocketFactory(sslSocketFactory)
			.setDefaultAuthSchemeRegistry(authSchemeRegistry)
			.build()

		val httpGet = new HttpGet(path)

		log.info("Executing client with URL " + path)
		val httpResponse = httpClient.execute(httpGet)
		val statusLine = httpResponse.getStatusLine

		if (statusLine.getStatusCode != 200)
			throw new RuntimeException("Invalid status code " + statusLine.getStatusCode + " (" + statusLine.getReasonPhrase + ")")

		httpResponse.getEntity.getContent
	}

	def getWSDL(url: URL): InputStream = {
		resourceManager.getResourceAsStream(url.toString)
	}
}
