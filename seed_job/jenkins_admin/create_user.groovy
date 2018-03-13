import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.security.*
import hudson.model.*
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

//Get users.json 
def user_list_path = build.workspace.toString() + '/seed_job/files/users.json'
println ("$user_list_path")
def inputFile = new File("$user_list_path")
def user_list = new JsonSlurper().parse(inputFile)
println (user_list)

//Get current users
def instance = Jenkins.getInstance()
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def allUsers = hudsonRealm.getAllUsers()
def userIdList = []
allUsers.each{ user->
    userIdList.add(user.getId())
}
println ("$userIdList")
println ("$allUsers")

//Check if user property has changed
boolean CheckChanges() {
    //Not implement yet
}

//Ensure user present or absent
user_list.each{ user->
    if (user.name in userIdList) {
        if (user.state == 'present') {
            println ("$user.name already exists, skip creating...")
            CheckChanges()
        }
        else{
            println ("Removing $user.name")
            hudsonRealm.getUser($user.name).delete()
        }
    } else{
        if (user.state == 'present') {
            println ("Creating user $user.name...")
            hudsonRealm.createAccount("$user.name","$user.pass")
        }
        else{
            //Doing nothing
        }
    }
}
instance.setSecurityRealm(hudsonRealm)
instance.save()


