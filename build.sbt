name := "sbt-wsdl2java"
organization := "ee.risk.sbt.plugins"
version := "0.1.0"

scalaVersion := "2.10.6"
sbtPlugin := true

libraryDependencies += "org.apache.cxf" % "cxf-rt-transports-http" % "3.1.7"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2"
