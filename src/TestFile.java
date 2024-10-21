# Define the root folder where the structure will be created
$rootFolder = "C:\TestFolders"

# Create the root folder
New-Item -Path $rootFolder -ItemType Directory -Force

# Define a nested folder structure
$folderStructure = @(
    "$rootFolder\folder1",
    "$rootFolder\folder1\subfolder1",
    "$rootFolder\folder2",
    "$rootFolder\folder2\subfolder2",
    "$rootFolder\folder3"
)

# Create the nested folder structure
foreach ($folder in $folderStructure) {
    New-Item -Path $folder -ItemType Directory -Force
}

# Create some sample files in the folders
Set-Content -Path "$rootFolder\file1.txt" -Value "This is file1.txt"
Set-Content -Path "$rootFolder\folder1\file2.txt" -Value "This is file2.txt"
Set-Content -Path "$rootFolder\folder1\subfolder1\file3.txt" -Value "This is file3.txt"
Set-Content -Path "$rootFolder\folder2\subfolder2\file4.txt" -Value "This is file4.txt"

# Remove access to "folder2" for the current user to simulate permission denied
$folderToDeny = "$rootFolder\folder2"
$acl = Get-Acl $folderToDeny
$denyRule = New-Object System.Security.AccessControl.FileSystemAccessRule($env:UserName, "FullControl", "Deny")
$acl.SetAccessRule($denyRule)
Set-Acl $folderToDeny $acl

Write-Host "Folder structure created and permissions modified. 'folder2' is now inaccessible for the current user."
createFolderStructure.ps1

    # Restore access to folder2
$acl = Get-Acl "C:\TestFolders\folder2"
$acl.Access | Where-Object { $_.FileSystemRights -eq "FullControl" -and $_.AccessControlType -eq "Deny" } | ForEach-Object { $acl.RemoveAccessRule($_) }
Set-Acl "C:\TestFolders\folder2" $acl

Write-Host "Access restored to folder2."

    
