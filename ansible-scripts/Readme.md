# Ansible scripts

Ansible allows you to execute scripts (called Playbooks) on several EC2 instances at once, in parallel. This means that you launch the command once, and all your instances will execute that set of commands. This, coupled to the usage of ssh to connect to the instances, makes Ansible a perfect choice for managing your EC2 instances.

Ansible also provides an [Amazon API](ansible.cc/docs/amazon_web_services.html) that you can use to interact with AWS. That API is not used in this example as we want to provide basic functionality you can extend for your own project. 

This folder contains a set of [Ansible](http://ansible.cc/docs/) scripts organized following Ansible [best practices](http://ansible.cc/docs/bestpractices.html) which can be used to:

* configure your Amazon EC2 instance
* deploy your Play application into the instance

with the aim of baking a custom AMI that can be used for auto-scaling.


## Contents

This section lists the contents of this folder, explaining its purpose.

There are 3 configuration files which contain addresses of servers. They are used to tell Ansible against which servers do we want to execute a Playbook.

* development.hosts             Contains the addresses of all development servers
* testing.hosts                 Contains the addresses of all test servers
* production.hosts              Contains the addresses of all production servers

There are 3 main Playbooks available. Each one can be executed to set up either all our system (site.yml) or a specific subset of machines (web servers or db). The db Playbook is included here for completeness purposes. Most likely you will use Amazon RDS, which makes this unnecessary. But if you decide to deploy your own servers with your own database versions, you can use this to prepare that instance.

* site.yml			Playbook for the full site, includes the Playbooks for website and db servers. Allows us to execute all the playbooks with one command
* webservers.yml		Playbook for web servers, includes other Playbooks required to setup the servers	
* dbservers.yml			Playbook for database servers. Only used if you opt-out of RDS

There are 2 folders that contain specific configuration for the Playbooks, depending on the group or host we are targeting.

* group_vars                    Contains specific configuration for server groups (webservers, databases, all)
* host_vars                     Contains specific configuration for an individual host. Each file is named as the hostname for which we want to use that config

The roles folder contains the different 'roles'. A role is a set of Playbooks, templates and other files that do a specific setup in the server. For example, the 'common' role has a list of commands to secure a Linux server. Each role follows the Ansible [best practices](http://ansible.cc/docs/bestpractices.html).

* roles/common                  Contains Playbooks to setup an Ubuntu server with the basic applications and security settings
* roles/webtier                 Contains Playbooks to setup an Ubuntu webserver with all the tools needed to deploy a Play application, including custom `start` file.
* roles/dbtier                  Empty role. Can be used as a template if you want to deploy your own database server instead of using RDS

## How to use

Your first step will be to set the proper endpoints in each of the hosts files (development.hosts, testing.hosts, production.hosts), so the Playbooks are executed against the intended targets. These scripts currently contain some sample EC2 public IP as an example.

Next, you will need to set the right variable values in `groups_vars/all`, so the scripts know the location of the git repository with the project, etc.

You will also need to edit the `start` file located in the `files` folder of the `webtier` role, and ensure the right configuration for your Play application is in place. 

Finally, you have to run the scripts. You will need sudo rights in the target machine for this, and the command (assuming default Ubuntu EC2 AMI) will be:

    ansible-playbook -i production.hosts site.yml -u ubuntu --sudo

Note that this command assumes that your ssh keys are properly set up for ssh to connect to Amazon instances.


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




