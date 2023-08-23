export class ObjectUtils {
  static deepClone<T>(object: T): T {
    return JSON.parse(JSON.stringify(object));
  }
}
