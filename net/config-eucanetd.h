#ifndef INCLUDE_CONFIG_EUCANETD_H
#define INCLUDE_CONFIG_EUCANETD_H

#include <config.h>

enum { EUCANETD_CVAL_PUBINTERFACE, EUCANETD_CVAL_PRIVINTERFACE, EUCANETD_CVAL_BRIDGE, EUCANETD_CVAL_EUCAHOME, EUCANETD_CVAL_MODE, EUCANETD_CVAL_ADDRSPERNET, EUCANETD_CVAL_SUBNET, EUCANETD_CVAL_NETMASK, EUCANETD_CVAL_BROADCAST, EUCANETD_CVAL_DNS, EUCANETD_CVAL_DOMAINNAME, EUCANETD_CVAL_ROUTER, EUCANETD_CVAL_DHCPDAEMON, EUCANETD_CVAL_DHCPUSER, EUCANETD_CVAL_MACPREFIX, EUCANETD_CVAL_CC_POLLING_FREQUENCY, EUCANETD_CVAL_EUCA_USER, EUCANETD_CVAL_LOGLEVEL, EUCANETD_CVAL_LOGROLLNUMBER, EUCANETD_CVAL_LOGMAXSIZE, EUCANETD_CVAL_LAST };

configEntry configKeysRestartEUCANETD[] = {
  {"EUCALYPTUS", "/"},
  {"VNET_BRIDGE", NULL},
  {"VNET_BROADCAST", NULL},
  {"VNET_DHCPDAEMON", "/usr/sbin/dhcpd41"},
  {"VNET_DHCPUSER", "root"},
  {"VNET_DNS", NULL},
  {"VNET_DOMAINNAME", "eucalyptus.internal"},
  {"VNET_MODE", "EDGE"},
  {"VNET_NETMASK", NULL},
  {"VNET_PRIVINTERFACE", NULL},
  {"VNET_PUBINTERFACE", NULL},
  {"VNET_PUBLICIPS", NULL},
  {"VNET_PRIVATEIPS", NULL},
  {"VNET_ROUTER", NULL},
  {"VNET_SUBNET", NULL},
  {"VNET_MACPREFIX", "d0:0d"},
  {"EUCA_USER", "eucalyptus"},
  {NULL, NULL},
};

configEntry configKeysNoRestartEUCANETD[] = {
  {"CC_POLLING_FREQUENCY", "5"},
  {"LOGLEVEL", "INFO"},
  {"LOGROLLNUMBER", "10"},
  {"LOGMAXSIZE", "104857600"},
  {"LOGPREFIX", ""},
  {"LOGFACILITY", ""},
  {NULL, NULL},
};

#endif
