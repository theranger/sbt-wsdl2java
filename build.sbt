name := "sbt-wsdl2java"
organization := "ee.risk.sbt.plugins"
version := "0.1.0"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.10.6"
sbtPlugin := true

libraryDependencies += "com.sun.xml.ws" % "jaxws-tools" % "2.2.10"
