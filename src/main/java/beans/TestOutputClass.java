package beans;
import beans.DBManager;
import java.util.ArrayList;
import java.sql.SQLException;

public class TestOutputClass {
	public static void main(String args[]) {
		DBManager dbm=new DBManager();
		//dbm.testQuery();
//		System.out.println(dbm.accountCheck("testid"));
		//System.out.println(dbm.addUser("newuser","userpw","user@example.com"));
//		System.out.println(dbm.testQueryWithReturn());
//		Post[] result=dbm.getUserPosts("입력받은 작성자ID")
//				.toArray(new Post[0]);
//		for(int i=0; i<result.length; i++) {
//			System.out.println(Integer.toString(i+1)+"번째 게시물");
//			System.out.printf("postId=%d, writerId:%s, imgURL:%s, title:%s, content:%s\n",
//					result[i].getPostId(),result[i].getWriterId(),result[i].getImgURL(),result[i].getTitle(),result[i].getContent());
//		}
//		Post post=new Post();
//		post.setPostId(1);
//		post.setImgURL("example URL");
//		post.setTitle("수정된 게시글 제목 1");
//		post.setContent("수정된 게시글 내용 1");
//		try {
//			dbm.updatePost(post);
//			System.out.println("실행완료");
//		}
//		catch(SQLException e) {
//			e.printStackTrace();
//		}
//		User user=dbm.getUserInfo("user");
//		System.out.printf("id:%s pw:%s e-mail:%s",user.getId(),user.getPw(),user.getEmail());
//		User user=new User();
//		user.setId("user");
//		user.setPw("pw");
//		user.setEmail("newer Email");
//		try {
//			dbm.updateUser(user);
//			System.out.println("실행완료");
//		}
//		catch(DBUpdateException e) {
//			e.printStackTrace();
//		}
		//System.out.println(dbm.checkFavorite("idasdfsadfsafsasafd", 5));
//		try {
//			dbm.deleteFavorite("testid", 3);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("실행완료");
		Post[] result=dbm.searchPostByTitle("제목").toArray(new Post[0]);
		for(int i=0; i<result.length; i++) {
			System.out.println(Integer.toString(i+1)+"번째 게시물");
			System.out.printf("postId=%d, writerId:%s, imgURL:%s, title:%s, content:%s\n",
					result[i].getPostId(),result[i].getWriterId(),result[i].getImgURL(),result[i].getTitle(),result[i].getContent());
		}
	}
}
