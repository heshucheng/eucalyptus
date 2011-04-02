# Copyright (c) 2011, Eucalyptus Systems, Inc.
# All rights reserved.
#
# Redistribution and use of this software in source and binary forms, with or
# without modification, are permitted provided that the following conditions
# are met:
#
#   Redistributions of source code must retain the above
#   copyright notice, this list of conditions and the
#   following disclaimer.
#
#   Redistributions in binary form must reproduce the above
#   copyright notice, this list of conditions and the
#   following disclaimer in the documentation and/or other
#   materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# Author: Mitch Garnaat mgarnaat@eucalyptus.com

from boto.roboto.awsqueryrequest import AWSQueryRequest
from boto.roboto.param import Param
import eucadmin

class DescribeProperties(AWSQueryRequest):

    ServicePath = '/services/Properties'
    ServiceClass = eucadmin.EucAdmin
    Description = 'Get properties'

    def __init__(self, **args):
        AWSQueryRequest.__init__(self, **args)
        self.list_markers = ['euca:properties']
        self.item_markers = ['euca:item']
  
    def get_connection(self, **args):
        if self.connection is None:
            args['path'] = self.ServicePath
            self.connection = self.ServiceClass(**args)
        return self.connection
      
    def cli_formatter(self, data):
        props = data['euca:DescribePropertiesResponseType']['euca:properties']
        for prop in props:
            print 'PROPERTY\t%s\t%s' % (prop['euca:name'], prop['euca:value'])

def main(**args):
    req = DescribeProperties(**args)
    return req.send()


def main_cli():
    req = DescribeProperties()
    req.do_cli()