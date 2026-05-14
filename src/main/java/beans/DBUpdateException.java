package beans;

public class DBUpdateException extends Exception {
	public Integer updatedData;
	public DBUpdateException() {
		super("비정상적인 DB업데이트가 발생했습니다.");
	}
	public DBUpdateException(String errorString) {
		super(errorString);
	}
	public DBUpdateException(int updatedData) {
		super("비정상적인 DB업데이트가 발생했습니다. 요청된 업데이트는 1개이나, 총 "+
				updatedData+"개의 데이터가 업데이트 되었습니다. DB 테이블을 확인하세요.");
		this.updatedData=updatedData;
	}
}