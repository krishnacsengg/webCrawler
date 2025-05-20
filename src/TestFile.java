Automate Archiving of Old Run_* Folders After Run Creation

Jira Description
Objective:
Implement logic to automatically compress and archive older Run_* folders in the run directory when a new run is created and the number of existing runs exceeds 3.

Business Need:
To prevent uncontrolled growth of the run directory and manage disk space efficiently, older run data should be archived after a threshold is reached.

Acceptance Criteria:

When a new run folder (e.g., Run_810) is created:

The system should scan the base directory for folders named in the pattern Run_*.

If more than 3 Run_* folders exist:

The oldest folders (based on numerical suffix) should be selected for archiving.

These folders should be compressed into .zip format.

The compressed files should be moved to the /Archived subdirectory.

The original uncompressed folders should be deleted after successful compression.

Non-run folders such as Input and Archived should be ignored in the check.

Folder naming format is consistent as Run_<runId> where <runId> is a numeric value (e.g., Run_123)
