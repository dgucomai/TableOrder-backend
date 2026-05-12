package dgucomai.tableorder.dto;

public record ApiResDto<T>(boolean success, String code, String message, T data) {

  public static <T> ApiResDto<T> success(T data) {
    return new ApiResDto<>(true, "OK", "정상 처리되었습니다.", data);
  }

  public static <T> ApiResDto<T> success(T data, String message) {
    return new ApiResDto<>(true, "OK", message, data);
  }

  public static <T> ApiResDto<T> error(String code, String message) {
    return new ApiResDto<>(false, code, message, null);
  }
}
