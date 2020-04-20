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
  - name: kaniko
    workingDir: /tmp/jenkins
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
"""
) {
   node(POD_LABEL) {
    stage('Build and test') {
     checkout scm
     container('application-container') {
      sh 'chmod 777 mvnw'
      sh './mvnw clean install -B -U'
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
       RUN mkdir / home / jenkins / .m2 """
       sh '''#!/busybox/sh /
       kaniko/executor--context `pwd`--verbosity debug--destination docker.ultimaengineering.io/test from kaniko:latest '''
      }
     }
   }
  }