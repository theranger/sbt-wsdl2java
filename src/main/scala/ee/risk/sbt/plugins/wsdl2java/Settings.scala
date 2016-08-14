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

import sbt.Keys._
import sbt._

/**
	* Created by The Ranger (ranger@risk.ee) on 2016-08-14
	* for Baltnet Communications LLC (info@baltnet.ee)
	*/
class Settings(parser: WSDLParser) {
	import Settings.autoImport._

	lazy val defaults = Settings.defaults ++ Seq(wsdl2java := parser.parse(streams.value.log))
}

object Settings {
	import autoImport._

	object autoImport {
		lazy val cxfSettings = sbt.config("cxf")
		lazy val cxfVersion = settingKey[String]("Apache CXF version")
		lazy val wsdl2java = taskKey[Seq[File]]("Generates Java files from WSDL")
		lazy val url = settingKey[String]("URL where WSDL file is located")
		lazy val args = settingKey[String]("Default arguments for Apache CXF")
		lazy val path = settingKey[File]("Path to store artifacts")
	}

	val defaults: Seq[Def.Setting[_]] = Seq(
		cxfVersion := "3.1.2"
	)
}
