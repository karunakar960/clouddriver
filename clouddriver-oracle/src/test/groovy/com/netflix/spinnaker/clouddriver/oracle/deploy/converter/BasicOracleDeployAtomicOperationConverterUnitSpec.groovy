/*
 * Copyright (c) 2017 Oracle America, Inc.
 *
 * The contents of this file are subject to the Apache License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * If a copy of the Apache License Version 2.0 was not distributed with this file,
 * You can obtain one at https://www.apache.org/licenses/LICENSE-2.0.html
 */
package com.netflix.spinnaker.clouddriver.oracle.deploy.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.clouddriver.deploy.DeployAtomicOperation
import com.netflix.spinnaker.clouddriver.oracle.deploy.description.BasicOracleDeployDescription
import com.netflix.spinnaker.clouddriver.oracle.security.OracleNamedAccountCredentials
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsProvider
import spock.lang.Shared
import spock.lang.Specification

class BasicOracleDeployAtomicOperationConverterUnitSpec extends Specification {

  private static final APPLICATION = "spinnaker"
  private static final STACK = "spinnaker-test"
  private static final FREE_FORM_DETAILS = "detail"
  private static final TARGET_SIZE = 3
  private static final IMAGE = "debian-7-wheezy-v20140415"
  private static final INSTANCE_TYPE = "f1-micro"
  private static final ZONE = "us-central1-b"
  private static final ACCOUNT_NAME = "auto"

  @Shared
  ObjectMapper mapper = new ObjectMapper()

  @Shared
  BasicOracleDeployAtomicOperationConverter converter

  def setupSpec() {
    this.converter = new BasicOracleDeployAtomicOperationConverter(objectMapper: mapper)
    def accountCredentialsProvider = Mock(AccountCredentialsProvider)
    def mockCredentials = Mock(OracleNamedAccountCredentials)
    accountCredentialsProvider.getCredentials(_) >> mockCredentials
    converter.accountCredentialsProvider = accountCredentialsProvider
  }

  void "basicOracleDeployDescription type returns BasicOracleDeployDescription and DeployAtomicOperation"() {
    setup:
    def input = [application : APPLICATION,
                 stack       : STACK,
                 capacity    : [desired: 1],
                 image       : IMAGE,
                 instanceType: INSTANCE_TYPE,
                 zone        : ZONE,
                 credentials : ACCOUNT_NAME]

    when:
    def description = converter.convertDescription(input)

    then:
    description instanceof BasicOracleDeployDescription

    when:
    def operation = converter.convertOperation(input)

    then:
    operation instanceof DeployAtomicOperation
  }

  void "basicOracleDeployDescription type with free-form details returns BasicOracleDeployDescription and DeployAtomicOperation"() {
    setup:
    def input = [application    : APPLICATION,
                 stack          : STACK,
                 freeFormDetails: FREE_FORM_DETAILS,
                 targetSize     : TARGET_SIZE,
                 image          : IMAGE,
                 instanceType   : INSTANCE_TYPE,
                 zone           : ZONE,
                 credentials    : ACCOUNT_NAME]

    when:
    def description = converter.convertDescription(input)

    then:
    description instanceof BasicOracleDeployDescription

    when:
    def operation = converter.convertOperation(input)

    then:
    operation instanceof DeployAtomicOperation
  }

  void "should not fail to serialize unknown properties"() {
    setup:
    def input = [application: application, unknownProp: "this"]

    when:
    def description = converter.convertDescription(input)

    then:
    description.application == application

    where:
    application = "kato"
  }

}
