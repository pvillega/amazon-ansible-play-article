# Ansible Playbooks

Ansible allows you to execute scripts (called Playbooks) on several EC2 instances at once, in parallel. This means that you launch the command once, and all your instances will execute that set of commands. This, coupled to the usage of ssh to connect to the instances, makes Ansible a perfect choice for managing your EC2 instances.

Ansible also provides an [Amazon API](ansible.cc/docs/amazon_web_services.html) that you can use to interact with AWS. In these scripts we take advantage of this API to deploy the application in AWS.

This folder contains a set of [Ansible](http://ansible.cc/docs/) Playbooks organized following Ansible [best practices](http://ansible.cc/docs/bestpractices.html) which can be used to:

* create a custom AMI
* use CloudFormation to deploy your application in AWS

Read the documentation for more details on how to use the Playbooks

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




