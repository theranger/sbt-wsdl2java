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

import sbt._
import sbt.plugins.JvmPlugin

/**
	* Created by The Ranger (ranger@risk.ee) on 2016-08-14
	* for Baltnet Communications LLC (info@baltnet.ee)
	*/
object Wsdl2Java extends AutoPlugin with WSDLParser {
	override def requires = JvmPlugin
	override def trigger = allRequirements

	private val settings = new Settings(this)
	private val cxfClient = new CXFClient(new File("temp"))
	override val projectSettings = inConfig(Compile)(settings.defaults)

	override def parse(log: Logger): Seq[File] = {

		cxfClient.getWSDL(new URL("https://localhost:44381/HelloWorld.svc?wsdl"))
		Nil
	}
}
