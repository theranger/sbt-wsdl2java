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

import java.io.InputStream

import org.apache.cxf.BusFactory
import org.apache.cxf.resource.{ResourceManager, ResourceResolver}
import org.apache.http.client.methods.HttpGet
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import sbt.{File, URL}

/**
	* Created by The Ranger (ranger@risk.ee) on 2016-08-14
	* for Baltnet Communications LLC (info@baltnet.ee)
	*/
class CXFClient(directory: File) extends ResourceResolver {
	private lazy val sslClient = new SSLClient(directory)
	private lazy val bus = BusFactory.getDefaultBus()
	private lazy val resourceManager = bus.getExtension(classOf[ResourceManager])
	resourceManager.addResourceResolver(this)

	protected override def resolve[T](resourceName: String, resourceType: Class[T]): T = {
		resourceType.asInstanceOf
	}

	protected override def getAsStream(path: String): InputStream = {
		if (!path.startsWith("https")) return null

		lazy val sslFactory = SSLConnectionSocketFactory.getSocketFactory

		lazy val registry = RegistryBuilder.create[ConnectionSocketFactory]()
		registry.register("https", sslFactory)

		lazy val clientBuilder = HttpClientBuilder.create()
		clientBuilder.setSSLSocketFactory(sslFactory)
		clientBuilder.setSSLContext(sslClient.loadTrustStore("truststore.jks"))
		clientBuilder.setConnectionManager(new BasicHttpClientConnectionManager(registry.build()))

		lazy val httpGet = new HttpGet(path)
		clientBuilder.build().execute(httpGet).getEntity.getContent
	}

	def getWSDL(url: URL): InputStream = {
		sslClient.queryCertificates(url, "truststore.jks")
		resourceManager.getResourceAsStream(url.toString)
	}
}
