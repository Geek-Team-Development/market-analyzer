import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {UserDto} from "../dto/user-dto";
import {USERS} from "../config/backend-urls";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private http: HttpClient) { }

  public getUsers(): Observable<UserDto[]> {
    return this.http.get(USERS)
      .pipe(
        map(response => {
          return response as UserDto[];
        })
      );
  }

  public getUser(id: string) {
    return this.http.get(USERS + `/${id}`)
      .pipe(
        map(response => {
          return response as UserDto;
        })
      );
  }

  public updateUser(userDto: UserDto) : Observable<UserDto> {
    return this.http.put(USERS, userDto)
      .pipe(
        map(response => {
          return response as UserDto;
        })
      )
  }

  public updatePassword(id: string | null, password: string) {
    return this.http.put(USERS + '/' + id, password)
      .pipe(
        map(response => {
          return response as UserDto;
        })
      )
  }
}
