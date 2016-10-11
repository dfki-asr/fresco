# fReSCO
fReSCO (Rdf Syntax COnversion) is yet another multi-format RDF conversion REST API.

## Features
Currently, fReSCO supports the following RDF syntaxes:
* JSON-LD
* N3
* N-Quads
* N-Triples
* RDF-JSON
* RDF/XML
* TRIG
* TRIX
* Turtle
	
## Installation & Running
Run fReSCO on your localhost by typing
```
mvn clean package tomcat7:run
```

You can modify fReSCO listening port in the `pom.xml`
```xml
<build>
	...
	<plugins>
		...
		<plugin>
			<groupId>org.apache.tomcat.maven</groupId>
			<artifactId>tomcat7-maven-plugin</artifactId>
			<version>2.2</version>  	
            		<configuration>
				<server>fReSCO</server>
				<port>8081</port>
				<path>/</path>
			</configuration>
		</plugin>
		...
	</plugins>
</build>
```

## Usage
Get fReSCO up and running. Then, use your favourite REST client against one of our REST APIs.

### HTML UI
Point your good ol' browser to `http://localhost:8081` for some self-explanatory HTML UI.

### Form-style GET API
HTTP GET requests can be made against fReSCO's form-style GET API as follows
```
GET api/{importFormat}/{exportFormat}?uri={documentURI} HTTP/1.1
```
where `importFormat` is format of your RDF document hosted under `documentURI` and `exportFormat` is your desired RDF export syntax.

You can choose from the following format options:
```
json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle
```

### Direct POST API
HTTP POST requests can be made against fReSCO's direct POST API as follows
```
POST api/{importFormat}/{exportFormat} HTTP/1.1
HOST: localhost:8081

{your_RDF_goes_here}
```
where `importFormat` is format of `your_RDF_goes_HERE` and `exportFormat` is your desired RDF export syntax.

You can choose from the following format options:
```
json-ld|n3|n-quads|n-triples|rdf-json|rdf-xml|trig|trix|turtle
```

## Contributing
Contributions are very welcome.

## License
This source distribution is subject to the license terms in the LICENSE file found in the top-level directory of this distribution.
You may not use this file except in compliance with the License.

## Third-party Contents
This source distribution includes the third-party items with respective licenses as listed in the THIRD-PARTY file found in the top-level directory of this distribution.

## Acknowledgements
This work has been supported by the [German Ministry for Education and Research (BMBF)](http://www.bmbf.de/en/index.html) (FZK 01IMI3001 J) as part of the [ARVIDA](http://www.arvida.de/) project.
