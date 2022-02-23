package googledocs;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDrive {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	/** Directory to store authorization tokens for this application. */
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	
	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	
	/**
	 * Creates an authorized Credential object.
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GoogleDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		//returns an authorized Credential object.
		return credential;
	}
	
	public static void main(String... args) throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME)
				.build();
		/*
		// Print the names and IDs for up to 10 files.
		FileList result = service.files().list()
				.setPageSize(50)
				.setFields("nextPageToken, files(id, name)")
				.execute();
		List<File> files = result.getFiles();
		if (files == null || files.isEmpty()) {
			System.out.println("No files found.");
		} else {
			System.out.println("Files:");
			for (File file : files) {
				System.out.printf("%s (%s)\n", file.getName(), file.getId());
			}
		}
		*/
		
		/*
		String fileId = "1fGFi_rpBz9YgR8qmklHLU2EUi6CfebrP"; // KEY: AIzaSyDwW3LpJ3mIkDydAzBssTNdC2NGOYxUdHk | FILE: 1fGFi_rpBz9YgR8qmklHLU2EUi6CfebrP
		OutputStream outputStream = new FileOutputStream("C:\\Users\\LordBecher\\Downloads\\Drive\\file"); // previously ByteArrayOutputStream
		service.files().get(fileId)
				.executeMediaAndDownloadTo(outputStream);
		*/
		System.out.println(service.files().get("1fGFi_rpBz9YgR8qmklHLU2EUi6CfebrP").execute().values());
		
//		Letters by Languages (15EYEpG8HcCmYpFohQX-NReu42cCFvUA-puc9yxEZU6w)
//		Tribeverse (1PAGRLQj9QsdknTuMapii0BIr5au16AHDvTdbCPZnsnI)
//		Bot Requirements (12_bex-I9pplNzRmRm0e-zfVEBMZEA7VRC6GoO6fTM2Y)
//		Teilnehmerliste AFF Kurse (1md0yBghLbzepuVcSg9YM2cNxjghJWgK1bK2Z8A9560k)
//		map.pptx (1C-VssrQbmrf6jhsjPQlSQn7giUVQsD8S)
//		events.pptx (1NdM-brRXeV1TzDGcy2V2OgdUmDji_cQ8)
//		Tribeverse (1pQi7xVm3IIQ-pfw_0gSkA3biwndIJLCfZjCZAywP-nI)
//		Cymanti (16kuGGHGiCGnel9SOJ6dgrFOuWCT5pEas)
//		测试无线接收器参数操作流程EN_x264.mp4 (1vaPllKBXa_h1Bpf4tFS4sbZUq0ha7cbv)
//		game_dev_intro.mp4 (10PTYXlnAhoSjnJUjFi7xTecW-_pl65-v)
//		Spreadsheet Meetup (1mcN-eGXaLdSNtE6Kxos0F3YqM75cP8zrdliOfxOgrZo)
//		Unbenanntes Dokument (1rHTPPab7piCCDbPeUdo6Qmi8di01eZypz4YPb18LrmM)
//		sausagemurderer (1TRtdjKnM6CTEYGfCg_WoDHrf__hvZqbgNnp_u9Fk9zo)
//		Komische lootboy codes selbstbedienung (1eHInh5dz8gjCeOqqKVGGVtGMkHfQzuF6tN9eO5RQ3bY)
//		HuionSerialNumberWriter.exe (1vJkmdWl0qZ6vlYOw1qi8qJagWm9gzNLI)
//		Unbenannte Tabelle (1g1kQYomspmuyAJrpMjmdJMYhSHhZLSRG_w2SDh318oc)
//		Boom HeadShot Song.mp3 (14p2PonX85oUt3MIiK-MEpTCEl-Ba5MPv)
//		präsi.mp4 (1VnaC9GHNeqUg1GKGeKzl-qhU0IoxhBn-)
//		#team_cc (1T8f75Et9CSF7JIU5vJcvrlRov_xk34ZUFrAsVnt83QY)
//		LogoCCgreen.png (0B6Q6e7Jvm7Qib200clNBS3Fjdms)
//		DIALOG (1OK2amyMJD3A5v-BZaKtDyF3eePrqtsD782EROrnH5Kk)
//		presentation_cc (1ZXcY8_Fpb6YjmxPT7GLWJYpPndXbGZNOWzZxRoONrrc)
//		storyboard (19I97-2Cx0LCbMhjRZ2Mbl163OhoeCwT0RFhwPesl2ro)
//		english.pdf (0B4bpzSoM_7w6VEJnb0JRV3BPMjA)
//		GTA Online Businesses (1mX8wGG1Oy76nhv-tBZ-oPmKiynng8fMS87EMxmoyB0Y)
//		Albion Online Cd-Key Serial Generator PC Download (15WMuiiyYTE_ils7vRLP81cxs2WM)
//		Berlin-Ostbahnhof (1373FR3wG4L1Crk1R_IEwq22oNrg)
	}
	
	// File: 1fGFi_rpBz9YgR8qmklHLU2EUi6CfebrP
	/*
	@SneakyThrows
	public static void main(String[] args) {
		final String key = "AIzaSyDwW3LpJ3mIkDydAzBssTNdC2NGOYxUdHk";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String s = reader.readLine();
			if (s.equals("exit")) {
				return;
			}
			
			//Code here
			printFileToConsole(s, key);
		}
	}
	
	private static void printFileToConsole(String file, String key) {
	
	}
	*/
}
