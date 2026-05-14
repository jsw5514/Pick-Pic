package beans;

import java.lang.String;

public class Post {
	Integer postId=null;//게시물 id
	String writerId=null;//작성자 id
	String imgURL=null;//이미지 파일 경로
	String title=null;//게시물 제목
	String content=null;//게시물 내용
	
	/* auto-generated getter and setter */
	public Integer getPostId() {
		return postId;
	}
	public void setPostId(Integer postId) {
		this.postId = postId;
	}
	public String getWriterId() {
		return writerId;
	}
	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
