# bitbucket-approve-plugin

Plugin that enables Jenkins to approve commits on Bitbucket. In a sense, you may compare it to how Travis and Github are working together.

## Configuration

The plugin creates a global configuration option to supply a username and a password. That user needs to have write access to your Bitbucket Repository.

## Usage

In your job, create a Post-Build-Step called **Approve commit on bitbucket**. Configure that step and set the **repository owner** and the **repository name**.

## Questions, suggestions?

Don't hesitate to file an issue or a pull request. This is my first attempt to creating a Jenkins plugin so appreciate any suggestions.

Cheers
