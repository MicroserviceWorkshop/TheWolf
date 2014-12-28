node {
  def mvnHome = tool 'Maven 3.x'
  echo "Mvn Home: ${mvnHome}"
  env.PATH="${mvnHome}/bin:${env.PATH}"

  stage 'Dev'

  git branch: 'master', changelog: true, url: 'https://github.com/MicroserviceWorkshop/TheWolf.git'

  parallel(telesales: {
    dir('telesales') {
      sh 'mvn clean package'
      archive 'target/telesales-0.0.1-SNAPSHOT.jar'
    }
  }, sales: {
    dir('sales') {
      sh 'mvn clean package'
      archive 'target/sales-0.0.1-SNAPSHOT.jar'
    }
  })

}