<!--[![Build Status](https://app.travis-ci.com/davidebasile/ContractAutomataLib.svg?branch=code-cleaning)](https://app.travis-ci.com/davidebasile/ContractAutomataLib)-->
![CodeQL](https://github.com/ContractAutomataProject/ContractAutomataLib/actions/workflows/codeql-analysis.yml/badge.svg)
![Build and Testing](https://github.com/ContractAutomataProject/ContractAutomataLib/actions/workflows/build.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/ContractAutomataProject/ContractAutomataLib/badge.svg?branch=main)](https://coveralls.io/github/ContractAutomataProject/ContractAutomataLib?branch=main)
[![Code Quality Score](https://api.codiga.io/project/32018/score/svg)](https://app.codiga.io/public/project/32018/ContractAutomataLib/dashboard)
[![Code Grade](https://api.codiga.io/project/32018/status/svg)](https://app.codiga.io/public/project/32018/ContractAutomataLib/dashboard)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Sonatype Nexus](https://img.shields.io/nexus/r/io.github.davidebasile/ContractAutomataLib?server=https%3A%2F%2Fs01.oss.sonatype.org%2F)](https://s01.oss.sonatype.org/content/repositories/releases/io/github/davidebasile/ContractAutomataLib/0.0.1/)
[![Maven Central Repository](https://img.shields.io/maven-central/v/io.github.davidebasile/ContractAutomataLib)](https://repo1.maven.org/maven2/io/github/davidebasile/ContractAutomataLib/0.0.1/)
[![javadoc](https://javadoc.io/badge2/io.github.davidebasile/ContractAutomataLib/javadoc.svg)](https://javadoc.io/doc/io.github.davidebasile/ContractAutomataLib)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/davidebasile/ContractAutomataLib)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=ContractAutomataProject_ContractAutomataLib)
[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=ContractAutomataProject_ContractAutomataLib)](https://sonarcloud.io/summary/new_code?id=ContractAutomataProject_ContractAutomataLib)
<!--[![GitHub issues](https://img.shields.io/github/issues/davidebasile/ContractAutomataLib)](https://github.com/davidebasile/ContractAutomataLib/issues)-->

<h1>Contract Automata Lib </h1>

The Contract Automata Toolkit is an ongoing basic research activity about implementing 
and experimenting with new developments in the theoretical framework of Contract Automata (CA). 
This repository contains the Contract Automata Library, which is the main repository of the Contract Automata Toolkit.
Contract automata are a formalism developed in the research area of foundations for services and distributed 
computing.
They are used for specifying services' interface, called behavioral contracts, 
 as finite state automata, with functionalities for composing contracts and generating the 
 orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product 
 lines (FMCA).

The source code has been redesigned and refactored using the new functionalities introduced with Java 8 (streams, lambda).

<h2>License</h2>
The tool is available under <a href="https://www.gnu.org/licenses/gpl-3.0">GPL-3.0 license</a>.

<h2> API Documentation</h2>

This software has been developed also using the Model-based Software Engineering tool Sparx Enterprise Architect. 
The following documentation is up-to date to the commit <a href="https://github.com/contractautomataproject/ContractAutomataLib/commit/d92c4b6f73d157b163f0df97d0192dbd6d26b252">d92c4b6 of 8 December 2021</a>. 

<ul>
  <li> <a href="https://contractautomataproject.github.io/ContractAutomataLib/site/index.htm">Online documentation</a>
</li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CAT_Lib_diagrams.pdf">Diagram report (pdf)</a></li>
  <li><a href="https://contractautomataproject.github.io/ContractAutomataLib/doc/CAT_Lib_doc.pdf">Library report (pdf)</a></li>
</ul> 

The javadoc documentation for the release to the Maven Central Repository is available at <a href="https://javadoc.io/doc/io.github.davidebasile/ContractAutomataLib/latest/overview-summary.html">https://javadoc.io/doc/io.github.davidebasile/ContractAutomataLib/latest/overview-summary.html</a>.


<h2>User Documentation</h2>

The user documentation, containing information on the usage and installation of the library, is available at the Github Page https://contractautomataproject.github.io/ContractAutomataLib/.


<h2>Branches</h2> 
This is the <tt>main</tt> branch of the repository. 

The github page of this repository is hosted in the branch <tt>gh-pages</tt>.

The branch <tt>old-backup</tt> contains a legacy version of the code, prior to the refactoring. The old version is kept as a reference for the papers published prior to the refactoring. If you are reaching this repository from our JSCP2020 or LMCS2020 papers you may want to check the <tt>old-backup</tt> branch where the case studies described in these papers are available under the folders <tt>demoJSCP</tt> and <tt>demoLMCS2020</tt>.

<h2>Contacts</h2>

If you have any question contact me on davide.basile@isti.cnr.it.

