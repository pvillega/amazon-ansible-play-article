# Given a host with specific settings and assuming its name is the same as this file's name (ec2-72-44-33-67.compute-1.amazonaws.com)
# Then we can define some variables in here that will be picked by the Playbooks when accessing "ec2-72-44-33-67.compute-1.amazonaws.com" 

# NOTE: try to avoid doing this, as it may cause problems long term. But if you really need it, use it.

# host-specific-setting: value
---
machineVar: value
