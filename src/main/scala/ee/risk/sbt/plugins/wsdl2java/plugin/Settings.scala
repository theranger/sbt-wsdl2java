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

import ee.risk.sbt.plugins.wsdl2java.wsdl.WSDLParser
import sbt.Keys._
import sbt._

/**
 * Created by The Ranger (ranger@risk.ee) on 2016-08-14
 * for Baltnet Communications LLC (info@baltnet.ee)
 */
object Settings {
	import autoImport._

	object autoImport {
		lazy val wsdl2java = config("wsdl2java") extend Compile

		lazy val parseWSDL = taskKey[Unit]("Generates Java files from WSDL")
		lazy val rootDir = settingKey[String]("Parent directory that will hold the artifacts subtrees (default: app)")

		lazy val getCertificates = taskKey[Unit]("Get certificates for URL list")
		lazy val urls = settingKey[Map[String, String]]("List of <wsdl, package_name> pairs")
		lazy val trustStore = settingKey[String]("Path to trust store file (default: conf/truststore.jks")
	}

	def defaults(parser: WSDLParser): Seq[Def.Setting[_]] = Seq(
		urls := Map[String, String](),
		rootDir := "app",
		trustStore := "conf" + Path.sep + "truststore.jks",
		parseWSDL := parser.parseWSDL(streams.value.log, rootDir.value, urls.value, new File(trustStore.value)),
		getCertificates := parser.queryCertificates(streams.value.log, urls.value, new File(trustStore.value))
	)
}
