package beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.stream.IntStream;
import java.util.ArrayList;

public class DBManager {
	private static Connection conn=null;
	private static PreparedStatement statement=null;
	private final String JDBC_URL="jdbc:mysql://localhost:3306/jspdb";
	
	//DB질의문에서 파라미터의 타입 문제를 해결하기위한 내부클래스
	abstract static class StatementParm{
		abstract void setParm(int index) throws SQLException;
	}
	static class IntParm extends StatementParm{
		public Integer value=null;
		IntParm(Integer value){ this.value=value; }
		@Override
		void setParm(int index) throws SQLException {
			statement.setInt(index,value);
		}
	}
	static class StringParm extends StatementParm{
		public String value=null;
		StringParm(String value) { this.value=value; }
		@Override
		void setParm(int index) throws SQLException {
			statement.setString(index, value);
		}
	}
	public static IntParm ip(Integer value) { return new IntParm(value); }
	public static StringParm sp(String value) { return new StringParm(value); }
	

	
	//DB 연결 시작
	void open(){
		//JDBC 4.0 이상이므로 명시적 드라이버 호출은 생략해도 됨
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");//명시적 드라이버 로딩
			conn=DriverManager.getConnection(JDBC_URL,"server","passwd");
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	//DB 연결 종료
	void close() {
		try {
			statement.close();
			conn.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	//지정된 테이블 탐색 질의 싱행(커스텀 래퍼클래스 사용)
	private ResultSet executeQueryWithParm(String query, StatementParm... args) {
		try {
			//리소스 준비
			this.open();
			statement=conn.prepareStatement(query);
			
			//쿼리에 받은 파라미터 설정
			IntStream.range(0, args.length).forEach(i->{
				try {
					args[i].setParm(i+1);
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			});
			ResultSet result=statement.executeQuery();//쿼리 실행
			return result;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//지정된 테이블 변경 질의 싱행(커스텀 래퍼클래스 사용)
	//note: 예외발생시 null을 리턴하고 싶어서 Integer 래퍼로 리턴 
	private Integer executeUpdateWithParm(String query, StatementParm... args) {
		try {
			//리소스 준비
			this.open();
			statement=conn.prepareStatement(query);
			
			//쿼리에 받은 파라미터 설정
			IntStream.range(0, args.length).forEach(i->{
				try {
					args[i].setParm(i+1);
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			});
			int result=statement.executeUpdate();//쿼리 실행
			return result;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	//지정된 테이블 탐색 질의 싱행
	private ResultSet executeQuery(String query, String[] args) {
		try {
			//리소스 준비
			this.open();
			statement=conn.prepareStatement(query);
			
			//쿼리에 받은 파라미터 설정
			IntStream.range(0, args.length).forEach(i->{
				try {
					statement.setString(i+1, args[i]);
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			});
			ResultSet result=statement.executeQuery();//쿼리 실행
			return result;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//지정된 테이블 변경 질의 실행
	//note: 예외발생시 null을 리턴하고 싶어서 Integer 래퍼로 리턴 
	private Integer executeUpdate(String query, String[] args) {
		try {
			//리소스 준비
			this.open();
			statement=conn.prepareStatement(query);
			
			//쿼리에 받은 파라미터 설정
			IntStream.range(0, args.length).forEach(i->{
				try {
					statement.setString(i+1, args[i]);
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			});
			int result=statement.executeUpdate();//쿼리 실행
			return result;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//ResultSet에서 Post를 가져와서 ArrayList로 리턴
	private ArrayList<Post> getPostFromResult(ResultSet result){
		ArrayList<Post> resultPost=new ArrayList<Post>();
		Post temp=null;
		try {
			while(result.next()) {
				temp=new Post();
				temp.setPostId(result.getInt("ID"));
				temp.setWriterId(result.getString("WRITER"));
				temp.setImgURL(result.getString("IMG"));
				temp.setTitle(result.getString("TITLE"));
				temp.setContent(result.getString("CONTENT"));
				resultPost.add(temp);
			}
			return resultPost;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				result.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			};
			this.close();
		}
	}
	
	/*여기부터 컨트롤러를 위한 함수들*/
	
	//user id로 pw를 검색하는 함수
	/* 처리가능 요청: 로그인(login.jsp), ID 중복 확인(signUp.jsp)
	 * 입력 데이터: 유저 id
	 * 출력 데이터: (id에 맞는 유저를 발견한 경우 해당 유저의)유저 pw
	 *	 		 (id에 맞는 유저를 발견하지 못한 경우, 예외발생시)null
	 * */
	public String accountCheck(String id) {
		final String sql="SELECT PW FROM USERINFO WHERE ID=?";
		ResultSet result=this.executeQueryWithParm(sql, sp(id));
		try {
			if(result.next()) return result.getString("PW");
			else return null;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				result.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			};
			this.close();
		}
	}
	
	
	//새로운 회원정보를 등록하는 함수
	/* 처리가능 요청: 회원가입(signUp.jsp)
	 * 입력 데이터: 유저 id, 유저 pw, 유저 e-mail
	 * 출력 데이터: 없음
	 * */
	public void addUser(String id, String pw, String email) throws DBUpdateException {
		final String sql="INSERT INTO USERINFO(ID,PW,EMAIL) VALUES(?, ?, ?)";
		Integer result=this.executeUpdateWithParm(sql, sp(id), sp(pw), sp(email));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}	
	
	//번호 범위의 게시물을 리턴하는 함수
	/* 처리가능 요청: 번호가 연속된 복수의 게시물 요청(main.jsp)
	 * 입력 데이터: 게시물 시작번호, 끝번호
	 * 출력 데이터: (정상 처리된 경우) 게시물 목록 (ArrayList<Post> 타입) 
	 * 			 (내부적으로 예외가 발생하는 등 처리에 실패한 경우) null
	 * */
	public ArrayList<Post> getPostRange(int start, int end) {
		final String sql="SELECT * FROM POST WHERE ID BETWEEN ? AND ?";
		ResultSet result=this.executeQueryWithParm(sql, ip(start), ip(end));
		return getPostFromResult(result);
	}	
	
	//게시글 정보를 받아 db에 저장하는 함수
	/* 처리가능 요청: 게시물 작성 요청(writePost.jsp)
	 * 입력 데이터: 게시글 정보(Post 객체, 게시글 id는 없어도 됨)
	 * 출력 데이터: 없음
	 * */
	public void writePost(Post post) throws DBUpdateException {
		final String sql="INSERT INTO POST(WRITER,IMG,TITLE,CONTENT) VALUES(?, ?, ?, ?)";
		Integer result=this.executeUpdateWithParm(sql,
				sp(post.getWriterId()), sp(post.getImgURL()), sp(post.getTitle()),sp(post.getContent()));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}
	
	//특정 유저가 특정 게시물에 대해 favorite 설정이 되어있는지 확인하는 함수
	/* 처리가능 요청: 게시글이 favorite 설정 되어있는지 확인(favorite.jsp)
	 * 입력 데이터: 유저 id, 게시글 id
	 * 출력 데이터: (정상 처리된 경우)favorite 설정 여부(Boolean 타입)
	 * 			 (예외가 발생한 경우)null
	 */
	public Boolean checkFavorite(String userId, int postId) {
		final String sql=
				"SELECT * FROM FAVORITE WHERE USERID=? AND POSTID=?";
		ResultSet result=this.executeQueryWithParm(sql, sp(userId), ip(postId));
		Boolean isFavorite=null;
		try {
			isFavorite=result.next();
			result.close();
			return isFavorite;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//특정 게시글을 특정 유저의 favorite으로 설정하는 함수
	/* 처리가능 요청: 게시글 favorite 설정 요청(favorite.jsp)
	 * 입력 데이터: 유저 id, 게시글 id
	 * 출력 데이터: 없음
	 */
	public void setFavorite(String userId, int postId) throws DBUpdateException {
		final String sql="INSERT INTO FAVORITE(USERID,POSTID) VALUES(?, ?)";
		Integer result=this.executeUpdateWithParm(sql, sp(userId), ip(postId));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}
	
	//특정 게시글을 특정 유저의 favorite에서 삭제하는 함수
	/* 처리가능 요청: 게시글 favorite 삭제 요청(favorite.jsp)
	 * 입력 데이터: 유저 id, 게시글 id
	 * 출력 데이터: 없음
	 */
	public void deleteFavorite(String userId, int postId) throws DBUpdateException {
		final String sql="DELETE FROM FAVORITE WHERE USERID=? AND POSTID=?";
		Integer result=this.executeUpdateWithParm(sql, sp(userId), ip(postId));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}
	
	//user id로 favorite한 글을 검색하는 함수
	/* 처리가능 요청: favorite 설정한 게시글 요청(favorite.jsp)
	 * 입력 데이터: 유저 id
	 * 출력 데이터: (정상처리된 경우) favorite한 게시물 목록 (ArrayList<Post> 타입)
	 *	 		 (예외발생시)null
	 * */
	public ArrayList<Post> getFavoritePost(String userId) {
		final String sql="SELECT POST.* FROM POST JOIN FAVORITE ON POST.ID = FAVORITE.POSTID WHERE FAVORITE.USERID=?";
		ResultSet result=this.executeQueryWithParm(sql, sp(userId));
		return getPostFromResult(result);
	}
	
	//user id로 게시글을 검색하는 함수
	/* 처리가능 요청: 특정 유저가 작성한 글 전체 요청(myPost.jsp, myPage.jsp), 작성자 이름으로 게시글 검색(TopBar.jsp)
	 * 입력 데이터: 유저 id
	 * 출력 데이터: (정상처리된 경우) 유저가 작성한 게시물 목록 (ArrayList<Post> 타입)
	 *	 		 (예외발생시)null
	 * */
	public ArrayList<Post> getUserPosts(String userId){
		final String sql="SELECT *\r\n"
				+ "FROM POST\r\n"
				+ "WHERE WRITER=?";
		ResultSet result=this.executeQueryWithParm(sql, sp(userId));
		return getPostFromResult(result);
	}
	
	//수정된 게시글 정보를 받아 db에 저장된 정보를 수정하는 함수
	/* 처리가능 요청: 특정 게시글 수정 요청(writePost.jsp)
	 * 입력 데이터: 게시글 정보(Post 객체, 작성자 id는 필요없음))
	 * 출력 데이터: 없음
	 * */
	public void updatePost(Post post) throws DBUpdateException {
		final String sql="UPDATE POST SET IMG=?, TITLE=?, CONTENT=? WHERE ID=?";
		Integer result=this.executeUpdateWithParm(sql,
				sp(post.getImgURL()), sp(post.getTitle()),sp(post.getContent()), ip(post.getPostId()));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}
	
	//유저 id로 해당 유저의 정보를 검색하는 함수
	/* 처리가능 요청: 특정 유저의 정보요청(myPage.jsp)
	 * 입력 데이터: 유저 id
	 * 출력 데이터: (정상 처리된 경우) 유저 정보(User 객체)
	 * 			 (해당하는 유저가 없거나 예외가 발생한 경우) null
	 * */
	public User getUserInfo(String userId) {
		final String sql="SELECT * FROM USERINFO WHERE ID=?";
		ResultSet result=this.executeQueryWithParm(sql, sp(userId));
		User user=null;
		try {
			if(result.next()) {
				user=new User();
				user.setId(userId);
				user.setPw(result.getString("PW"));
				user.setEmail(result.getString("EMAIL"));
				return user;
			}
			else return null;//해당 유저를 발견하지 못하는 경우 null 리턴
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				result.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			};
			this.close();
		}
	}
	
	//수정된 유저 정보를 받아 db에 저장된 정보를 수정하는 함수
	/* 처리가능 요청: 특정 유저의 정보 수정(updatePersonalInfo.jsp)
	 * 입력 데이터: 유저 정보(User 객체))
	 * 출력 데이터: 없음
	 * */
	public void updateUser(User user) throws DBUpdateException {
		final String sql="UPDATE USERINFO SET PW=?, EMAIL=? WHERE ID=?";
		Integer result=this.executeUpdateWithParm(sql, sp(user.getPw()), sp(user.getEmail()), sp(user.getId()));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}
	
	//특정 번호 이후의 ID를 가진 게시물 중 상위 N개을 리턴하는 함수
	/* 처리가능 요청: 번호가 연속된 복수의 게시물 요청(main.jsp)-수정버전
	 * 입력 데이터: 게시물 시작번호, 원하는 게시물의 갯수
	 * 출력 데이터: (정상 처리된 경우) 게시물 목록 (ArrayList<Post> 타입) 
	 * 			 (내부적으로 예외가 발생하는 등 처리에 실패한 경우) null
	 * */
	public ArrayList<Post> getPostTop(int start, int num) {
		final String sql=
				"SELECT * FROM POST WHERE ID>? ORDER BY ID ASC LIMIT ?";
		ResultSet result=this.executeQueryWithParm(sql, ip(start), ip(num));
		return getPostFromResult(result);
	}
	
	//특정 번호의 게시글을 가져오는 함수
	/* 처리가능 요청: 게시글 id로 게시글 데이터 검색
	 * 입력 데이터: 게시물 id
	 * 출력 데이터: (정상 처리된 경우) 게시물 데이터
	 * 			 (내부적으로 예외가 발생하는 등 처리에 실패한 경우) null
	 * */
	public Post getPostById(int postId) {
		final String sql="SELECT * FROM POST WHERE ID=?";
		ResultSet result=this.executeQueryWithParm(sql, ip(postId));
		Post post=null;
		try {
			if(result.next()) {
				post=new Post();
				post.setPostId(postId);
				post.setWriterId(result.getString("WRITER"));
	            post.setImgURL(result.getString("IMG"));
	            post.setTitle(result.getString("TITLE"));
	            post.setContent(result.getString("CONTENT"));
				return post;
			}
			else {
				return null;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//게시글 제목으로 게시글을 검색하는 함수
	/* 처리가능 요청: 게시글 검색 요청(제목 기반)
	 * 입력 데이터: 검색어(게시글 제목의 substring)
	 * 출력 데이터: (정상처리된 경우) 검색된 게시물 목록 (ArrayList<Post> 타입)
	 *	 		 (검색결과가 없거나 예외발생시)null
	 * */
	public ArrayList<Post> searchPostByTitle(String substring) {
		final String sql="SELECT * FROM POST WHERE TITLE LIKE ?;";
		ResultSet result=this.executeQueryWithParm(sql, sp("%"+substring+"%"));
		return getPostFromResult(result);
	}
	
	//게시글 제목으로 게시글을 검색하는 함수
	/* 처리가능 요청: 게시글 검색 요청(제목 기반)
	 * 입력 데이터: 검색어(게시글 제목의 substring)
	 * 출력 데이터: (정상처리된 경우) 검색된 게시물 목록 (ArrayList<Post> 타입)
	 *	 		 (검색결과가 없거나 예외발생시)null
	 * */
	public ArrayList<Post> searchPostByWriter(String substring) {
		final String sql="SELECT * FROM POST WHERE WRITER LIKE ?";
		ResultSet result=this.executeQueryWithParm(sql, sp("%"+substring+"%"));
		return getPostFromResult(result);
	}
	
	//게시글 삭제함수
	public void deletePost(int postId) throws DBUpdateException {
		final String sql="DELETE FROM POST WHERE ID=?";
		Integer result=this.executeUpdateWithParm(sql, ip(postId));
		if(result!=null && result!=1) {
			throw new DBUpdateException(result);
		}
	}
	
	//쿼리 테스트 코드
	void testQuery() {
		final String query="SELECT *\r\n"
				+ "FROM POST\r\n"
				+ "WHERE ID BETWEEN ? AND ?";//"SELECT PW FROM USERINFO WHERE ID=?";
		ResultSet result=this.executeQuery(query, new String[]{"3","5"});
		try {
			while(result.next()) {
				System.out.println(result.getString("ID"));
				System.out.println(result.getString("WRITER"));
				System.out.println(result.getString("IMG"));
				System.out.println(result.getString("TITLE"));
				System.out.println(result.getString("CONTENT"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			this.close();
		}
		
//		this.open();
//		final String SQL="SELECT * FROM UserInfo";
//		
//		try {
//			statement=conn.prepareStatement(SQL);
//			result=statement.executeQuery();
//			result.next();
//			System.out.println("id="+result.getString("id")+" pw="+result.getString("pw")+" email="+result.getString("email"));
//			result.close();
//		}
//		catch(SQLException e) {
//			e.printStackTrace();
//		}
//		finally {
//			this.close();
//		}
	}
	public String testQueryWithReturn() {
		final String sql="SELECT PW FROM USERINFO WHERE ID=?";
		ResultSet result=this.executeQueryWithParm(sql, sp("testid"));//가변인자를 사용하므로 파라미터만 래핑해서 넣기
		try {
			if(result.next()) return result.getString("PW");
			else return null;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				result.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			};
			this.close();
		}
	}
}