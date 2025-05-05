### gRPC java learning

Just for demo purpose security certificate has been generated in src here.
In actual env this should be read from secrets or should be managed by **Istio**.

#### Before running maven build 

1. you need to run ssl script to generate certificates as some tests use TLS
2. make sure openssl is installed in your system
3. go to _src/test/resources/ssl_ 
4. run _chmod +x ssl.sh_ to make script executable
5. run _./ssl.sh_  it would generate tls certificates.

#### Some tests involve interaction with docker
1. pls turn on docker before running tests.

