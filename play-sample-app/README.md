# Play Sample App for EC2

This is a sample application to be deployed in [Amazon EC2](http://aws.amazon.com/ec2/).
The application consists on a simple website that loads photos from Flickr via a cron job and shows them to the user when asked.
It stores user credentials in MySQL (Amazon RDS) and the details of images in Amazon DynamoDB.

As a warning, this is a sample on how to build a Play application. This means that it may lack some guards that would be common
in a real application in production. Feel free to extend it as per your needs, but be aware that some areas could need improvement.

This webapp uses the following components:
* [Play 2.1.1 w/ Java](http://www.playframework.com/)
* [Webjars](http://www.webjars.org)
* [AWS SDK for Java](http://aws.amazon.com/sdkforjava/)
* [Play Authenticate](http://joscha.github.io/play-authenticate/)
* [Flickr API](http://www.flickr.com/services/api/)

# Configuration

The application follows the basic Play approach to configuration. File `conf\application.conf` contains the main configuration.
This includes some default configuration for Development as well as Flickr API keys, to facilitate the testing of this webapp.

Under `conf` folder there is a `play-authenticate` folder that contains 3 additional configuration files. `mine.conf` contains
all the authentication keys for 3rd party services like Twitter, while `smtp.conf` contains mail server details, which by default
uses a mock smtp server.

The AWS configuration has to be added under `conf\credentials.conf`. That file will override any settings in the main
`conf\application.conf` (except Flickr keys). You must add your AWS credentials to be able to connect to DynamoDB.

# Deployment

The application can be deployed using the standard procedure: `play dist` generates a zip file with the deployment structure
which you can copy to your server and unzip it in there. Execute the included `start` script to launch the webapp.

Remember that if a JVM environment variable (like `-Daws.key=<some_key>` is present, Play will use it instead of any configuration setting in the `conf` folder.

# WebJars

We use [Webjars](http://www.webjars.org) to pull Javascript files. This technology allows us to declare which Javascript frameworks we need
and it automatically pulls any dependencies they may have. This facilitates managing Javascript versions, ensuring compatibility and removing
the need of manual management of multiple Javascript files.

As an example, we load Twitter Bootstrap with:

      <link rel='stylesheet' href='@routes.WebJarAssets.at(WebJarAssets.locate("css/bootstrap.min.css"))'>
      <script type='text/javascript' src='@routes.WebJarAssets.at(WebJarAssets.locate("js/bootstrap.min.js"))'></script>


# Play Authenticate

[Play Authenticate](http://joscha.github.io/play-authenticate/) is used as the authentication and authorisation framework
(via its integration with [Deadbolt 2](https://github.com/schaloner/deadbolt-2)). It supports full I18N, with the strings being
defined at standard Play I18N files such as `conf\messages`.

The application has used content form the sample Play Authentication app to provide tha layout and basic functionality required for
authentication.

Play Authenticate allows the user to sign in to a page via common 3rd party authentication services like Twitter and Facebook.
If the user desires so, she can also access the site by providing a username and a password, although in this case be aware that
you will need to modify the `smtp` configuration or no email will be sent.

This application restricts access to the page that displays the images, which means that only authenticated users can see it.


# Cron tasks

The application includes an Akka Actor, `RetrieveTagsAndPhotos`, that runs once a day and saves into the database a list of popular
Flickr tags along photos referenced by these tags. The execution frequency is defined in `Global.java`, it runs one when the application
is started and then every 24 hours by default.

This background process in asynchronous, not blocking requests received by the application.


# Database

The application uses MySQL to store user information. That is, all the data required by Play Authenticate is stored in this database.
Application uses the default [EBean ORM](http://www.playframework.com/documentation/2.1.0/JavaEbean) from Play, which is a JPA compliant ORM.
The relevant classes are stored under `model` folder.

# Dynamo db

This application uses DynamoDB as a a storage for image information. The cron task retrieves a list of tags and related photos and stores them in Dynamo for easy access.
A service class `DynamoDBService` is provided to facilitate interaction with the Dynamo model used by the application.

Dynamo has 2 tables defined:
* Tag(id, name) (HashKey: id)
* Photo(idTag, dateStored, text, imageUrl, thumbnailUrl) (HashKey: idTag, RangeKey: dateStored)
All fields are String.


# License

Copyright 2013 Pere Villega

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
