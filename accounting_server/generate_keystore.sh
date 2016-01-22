#!/bin/bash

if [ $# == 0 ]; then
    exit 2
fi

password=$1

echo Generating certificate
openssl req -x509 \
    -newkey rsa:2048 \
    -keyout server.key \
    -out server.crt \
    -subj "/C=BE/O=InpresFPM" \
    -days 3650 \
    -nodes

echo Generating pkcs12 store
openssl pkcs12 -export \
    -in server.crt \
    -inkey server.key \
    -nodes \
    -out server.p12 \
    -name accounting_server \
    -CAfile ca.crt \
    -password pass:$password \
    -caname root

echo Converting to jks
keytool -importkeystore \
    -deststorepass $password \
    -destkeypass $password \
    -destkeystore server.jks \
    -srckeystore server.p12 \
    -srcstoretype PKCS12 \
    -srcstorepass $password \
    -alias accounting_server
