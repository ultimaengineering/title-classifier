podTemplate(yaml: """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: builder
spec:
  containers:
  - name: application-container
    image: docker.ultimaengineering.io/deeplearning_base:1.0.0
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: '/opt/app/shared'
      name: sharedvolume
  - name: kaniko
    workingDir: /tmp/jenkins
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
    - mountPath: '/opt/app/shared'
      name: sharedvolume
  volumes:
    - name: sharedvolume
      emptyDir: {}
"""
) {
   node(POD_LABEL) {
    stage('Build and test') {
     checkout scm
     container('application-container') {
      sh 'chmod 777 gradlew'
      sh './gradlew clean build'
      sh 'cp build/distributions/*.zip /opt/app/shared'
     }
    }
    stage('Build with Kaniko') {
     environment {
      PATH = "/busybox:/kaniko:$PATH"
     }
      container(name: 'kaniko', shell: '/busybox/sh') {

       writeFile file: "Dockerfile", text: """
       FROM docker.ultimaengineering.io:deeplearning_base:1.0.0
       MAINTAINER Alexander Montgomery
       RUN mkdir/home/jenkins/m2 """
       sh 'cd /opt/app/shared && ls'
       sh '/kaniko/executor -f `pwd`/Dockerfile -c `pwd` --cache=true --destination=docker.ultimaengineering.io/title-classifier'
      }
     }
   }
  }