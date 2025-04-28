#/bin/bash
export GENY_HOST=$(aws ec2 describe-instances --output=text --filters Name=instance-state-name,Values=running --query 'Reservations[].Instances[?Tags[?Key==`Name`].Value|[0]==`Genymotion`][].PublicDnsName')
export GENY_PASS=$(aws ec2 describe-instances --output=text --filters Name=instance-state-name,Values=running --query 'Reservations[].Instances[?Tags[?Key==`Name`].Value|[0]==`Genymotion`][].InstanceId')
export GENY_IP=$(aws ec2 describe-instances --output=text --filters Name=instance-state-name,Values=running --query 'Reservations[].Instances[?Tags[?Key==`Name`].Value|[0]==`Genymotion`][].'$GENY_IP_TARGET)
echo $GENY_HOST
echo $GENY_PASS
echo $GENY_IP
curl --insecure -X POST -u genymotion:$GENY_PASS https://$GENY_HOST/api/v1/configuration/adb \
    -H 'accept: application/json' -H 'Content-Type: application/json' -d '{"active": true,"active_on_reboot": true}'
sed -i 's|YOUR_HOST|'${GENY_IP}'|g' /root/.ssh/config
cat /root/.ssh/config
mkdir -p /home/androidusr/.ssh
cp -L /root/.ssh/config /home/androidusr/.ssh/config
cp -L /root/ssh/*.pem /home/androidusr/.ssh/cert.pem && chmod -R 600 /home/androidusr/.ssh/*