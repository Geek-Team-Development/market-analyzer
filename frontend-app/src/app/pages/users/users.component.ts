import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UsersService} from "../../services/users.service";
import {UserDto} from "../../dto/user-dto";
import {AuthService} from "../../services/auth.service";
import {FrontUrls} from "../../config/front-config";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  public users: UserDto[] = [];

  usersURL = '/' + FrontUrls.USERS;

  constructor(private userService: UsersService,
              public authService:AuthService) { }

  ngOnInit(): void {
    this.getUsers();
  }

  private getUsers(): void {
    this.userService.getUsers()
      .subscribe({
        next: value => {
          this.users = value;
        },
        error: err => {
          console.log(err);
        }
      });
  }
}
