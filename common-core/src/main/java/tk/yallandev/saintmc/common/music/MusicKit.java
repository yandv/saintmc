package tk.yallandev.saintmc.common.music;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum MusicKit {

	AUSTIN_WINTORY("https://www.dropbox.com/s/8uig1pyclgj3gl2/austin.zip?dl=1"),
	AWOLNATION("https://www.dropbox.com/s/tcfkghsnmrzu5js/awolnation.zip?dl=1"),
	NONE("https://www.dropbox.com/s/v1w9c8jqsk3uz3t/none.zip?dl=1");

	//https://download.saintmc.com.br/?name=austin

	private String downloadLink;

}
