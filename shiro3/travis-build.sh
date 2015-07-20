#!/bin/bash
set -e
cd shiro3
./grailsw refresh-dependencies --non-interactive --stacktrace
./grailsw test-app --non-interactive --stacktrace
