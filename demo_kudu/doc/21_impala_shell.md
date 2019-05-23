## impala-shell -i  gp-bd-slave01:21000


Options:
  -h, --help            show this help message and exit
  -i IMPALAD, --impalad=IMPALAD
                        <host:port> of impalad to connect to
                        [default: gp-bd-master01:21000]
  -q QUERY, --query=QUERY
                        Execute a query without the shell [default: none]
  -f QUERY_FILE, --query_file=QUERY_FILE
                        Execute the queries in the query file, delimited by ;
                        [default: none]
  -k, --kerberos        Connect to a kerberized impalad [default: False]
  -o OUTPUT_FILE, --output_file=OUTPUT_FILE
                        If set, query results are written to the given file.
                        Results from multiple semicolon-terminated queries
                        will be appended to the same file [default: none]
  -B, --delimited       Output rows in delimited mode [default: False]
  --print_header        Print column names in delimited mode when pretty-
                        printed. [default: False]
  --output_delimiter=OUTPUT_DELIMITER
                        Field delimiter to use for output in delimited mode
                        [default: \t]
  -s KERBEROS_SERVICE_NAME, --kerberos_service_name=KERBEROS_SERVICE_NAME
                        Service name of a kerberized impalad [default: impala]
  -V, --verbose         Verbose output [default: True]
  -p, --show_profiles   Always display query profiles after execution
                        [default: False]
  --quiet               Disable verbose output [default: False]
  -v, --version         Print version information [default: False]
  -c, --ignore_query_failure
                        Continue on query failure [default: False]
  -r, --refresh_after_connect
                        Refresh Impala catalog after connecting
                        [default: False]
  -d DEFAULT_DB, --database=DEFAULT_DB
                        Issues a use database command on startup
                        [default: none]
  -l, --ldap            Use LDAP to authenticate with Impala. Impala must be
                        configured to allow LDAP authentication.
                        [default: False]
  -u USER, --user=USER  User to authenticate with. [default: root]
  --ssl                 Connect to Impala via SSL-secured connection
                        [default: False]
  --ca_cert=CA_CERT     Full path to certificate file used to authenticate
                        Impala's SSL certificate. May either be a copy of
                        Impala's certificate (for self-signed certs) or the
                        certificate of a trusted third-party CA. If not set,
                        but SSL is enabled, the shell will NOT verify Impala's
                        server certificate [default: none]
  --config_file=CONFIG_FILE
                        Specify the configuration file to load options. File
                        must have case-sensitive '[impala]' header. Specifying
                        this option within a config file will have no effect.
                        Only specify this as a option in the commandline.
                        [default: /root/.impalarc]
  --live_summary        Print a query summary every 1s while the query is
                        running. [default: False]
  --live_progress       Print a query progress every 1s while the query is
                        running. [default: False]
  --auth_creds_ok_in_clear
                        If set, LDAP authentication may be used with an
                        insecure connection to Impala. WARNING: Authentication
                        credentials will therefore be sent unencrypted, and
                        may be vulnerable to attack. [default: none]
  --ldap_password_cmd=LDAP_PASSWORD_CMD
                        Shell command to run to retrieve the LDAP password
                        [default: none]
  --var=KEYVAL          Define variable(s) to be used within the Impala
                        session. [default: none]