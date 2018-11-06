Download the Java JRE if it's not already installed https://java.com/en/download/
Download ebird-flickr-downloader.jar
Open a command prompt
Switch to the directory that has ebird-flickr-downloader.jar
Argument 1 to the program is the directory of your "MyEBirdData.csv" file from https://ebird.org/downloadMyData
Argument 2 is your flickr username (so that photos from shared checklists aren't also downloaded)
Run the program using java -jar jarfilename arg1 arg2

EXAMPLE, if ebird-flickr-downloader.jar is in downloads directory and "MyEBirdData.csv" file is in documents/ebird/mydata
cd downloads
java -jar ebird-flickr-downloader.jar documents/ebird/mydata flickrusername
