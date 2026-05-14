package beans;

public class User {
	private String id=null;//user id
	private String pw=null;//user password
	private String email=null;//user e-mail
	
	public boolean signUp() {
		//TODO dao에 엑세스하여 데이터 처리
		//리턴값은 성공여부
		
		return false;
	}
	public void addFavorite(int postId) {
		
	}
	
	/* auto-generated getter and setter */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
