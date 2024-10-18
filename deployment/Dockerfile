FROM amazon/aws-cli:2.18.9

RUN yum -y update && yum -y install openssh-clients

ENTRYPOINT export GENY_HOST=$(aws ec2 describe-instances --output=text --filters Name=instance-state-name,Values=running --query 'Reservations[].Instances[?Tags[?Key==`Name`].Value|[0]==`Genymotion`][].PublicDnsName') && \
    export GENY_PASS=$(aws ec2 describe-instances --output=text --filters Name=instance-state-name,Values=running --query 'Reservations[].Instances[?Tags[?Key==`Name`].Value|[0]==`Genymotion`][].InstanceId') && \
    curl --insecure -X POST -u genymotion:$GENY_PASS https://$GENY_HOST/api/v1/configuration/adb \
    -H 'accept: application/json' -H 'Content-Type: application/json' -d '{"active": true,"active_on_reboot": true}'; \
    rm -rf /root/.ssh && mkdir /root/.ssh && cp -L /root/ssh/* /root/.ssh/ && ls -la /root/.ssh && chmod -R 600 /root/.ssh/* && \
    ssh $SSH_DEBUG -o StrictHostKeyChecking=no -N $TUNNEL_HOST -L *:$LOCAL_PORT:$REMOTE_HOST:$REMOTE_PORT && while true; do sleep 30; done;
