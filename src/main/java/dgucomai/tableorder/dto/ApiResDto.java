package dgucomai.tableorder.dto;

public record ApiResDto<T>(boolean success, T data, String message) {

  public static <T> ApiResDto<T> success(T data) {
    return new ApiResDto<>(true, data, null);
  }

  public static <T> ApiResDto<T> error(String message) {
    return new ApiResDto<>(false, null, message);
  }
}
