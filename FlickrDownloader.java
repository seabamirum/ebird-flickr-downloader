package fun.tcl.birds.flickr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.imageio.ImageIO;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.Size;

public abstract class FlickrDownloader 
{	
	private static void processPhoto(String speciesName,String subId,String photoId,PhotosInterface pi,String outputPath) throws FlickrException, IOException
	{
		String filePath = outputPath + File.separator + subId + "-" + speciesName + "-" + photoId + ".jpg";
		File outputfile = new File(filePath);
		
		if (outputfile.exists())
		{
			System.out.println(filePath + " already exists. Skipping...");
			return;
		}
		
		System.out.println("Writing flickr photo " + photoId + " to " + filePath + "...");
		
		Photo photo = pi.getPhoto(photoId);
		
		BufferedImage bufferedImage = pi.getImage(photo,Size.ORIGINAL);
		ImageIO.write(bufferedImage, "jpg", outputfile);
	}
	
	public static void main(String[] args) throws IOException
	{
		String outputPath = args[0];		
		String flickrUser=args[1];
		
		//https://www.flickr.com/services/api/misc.api_keys.html
		String apiKey = "xxx";
		String sharedSecret = "xxx";
		Flickr f = new Flickr(apiKey, sharedSecret, new REST());
		PhotosInterface pi = f.getPhotosInterface();
		
		try(FileInputStream myDataStream = new FileInputStream(outputPath + File.separator + "MyeBirdData.csv");
			CsvListReader csvListReader = new CsvListReader(new InputStreamReader(myDataStream,"UTF-8"),CsvPreference.STANDARD_PREFERENCE))
		{			
			csvListReader.getHeader(true);
			List<String> values;
			while ((values = csvListReader.read()) != null)
			{		
				if (values.size() <= 19)
					continue;
				
				String obsComments = values.get(19);
				
				if (obsComments != null && obsComments.contains("staticflickr.com"))
				{
					int urlBegin = obsComments.indexOf("/" + flickrUser);					
					while (urlBegin != -1) //can be multiple flickr links per OBS
					{
						urlBegin += flickrUser.length()+2;		
						
						//Some URLs may end with extra slash
						int urlEnd = obsComments.indexOf("/\"", urlBegin);
						if (urlEnd == -1)
							urlEnd = obsComments.indexOf("\"", urlBegin);
						
						String photoId = obsComments.substring(urlBegin,urlEnd);
						String speciesName = values.get(1).replace(" ","_");
						String subId=values.get(0);
						
						try
						{
							processPhoto(speciesName, subId, photoId, pi, outputPath);
						}
						catch(FlickrException fe)
						{
							System.err.println("Unable to fetch photo " + photoId + " from flickr! Continuing to next...");
						}
						
						urlBegin = obsComments.indexOf("/" + flickrUser,urlEnd);
					}
				}
			}
			
			System.out.println("All done! No more photos found.");
		}
	}
}
