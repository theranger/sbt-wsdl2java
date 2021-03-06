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
		lazy val wsdl2javaParseWSDL = taskKey[Unit]("Generates Java files from WSDL")
		lazy val wsdl2javaGetCertificates = taskKey[Unit]("Get certificates for URL list")

		lazy val wsdl2javaSourceRoot = settingKey[String]("Parent directory that will hold the artifacts subtrees (default: app)")
		lazy val wsdl2javaPathMap = settingKey[Map[String, String]]("Map of <wsdl, package_name> pairs")
		lazy val wsdl2javaTrustStoreFile = settingKey[String]("Path to trust store file (default: conf/truststore.jks")
		lazy val wsdl2javaBindingFiles = settingKey[Seq[String]]("List of custom binding files (default: empty")
		lazy val wsdl2javaAuthFile = settingKey[String]("Path to compatible auth file for HTTP basic authentication (default: none)")
	}

	def defaults(parser: WSDLParser): Seq[Def.Setting[_]] = Seq(
		wsdl2javaPathMap := Map[String, String](),
		wsdl2javaSourceRoot := "app",
		wsdl2javaTrustStoreFile := "conf" + Path.sep + "truststore.jks",
		wsdl2javaBindingFiles := Seq[String](),
		wsdl2javaAuthFile := "",

		wsdl2javaParseWSDL := parser.parseWSDL(
			streams.value.log,
			wsdl2javaSourceRoot.value,
			wsdl2javaPathMap.value,
			new File(wsdl2javaTrustStoreFile.value),
			wsdl2javaBindingFiles.value,
			wsdl2javaAuthFile.value),

		wsdl2javaGetCertificates := parser.queryCertificates(
			streams.value.log,
			wsdl2javaPathMap.value,
			new File(wsdl2javaTrustStoreFile.value))
	)
}
