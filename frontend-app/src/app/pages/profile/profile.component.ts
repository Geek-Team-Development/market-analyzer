import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../services/users.service";
import {UserDto} from "../../dto/user-dto";
import {ActivatedRoute, Router} from "@angular/router";
import {map, Observable} from "rxjs";
import {AuthService} from "../../services/auth.service";
import {FormBuilder, Validators} from "@angular/forms";
import {Roles} from "../../dto/roles";
import {FrontUrls} from "../../config/front-config";

const { email, required } = Validators;

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  private id: string | null = '';
  public profileForm = this.formBuilder.group({
    firstName: [null, required],
    lastName: [null, required],
    email: [null, [required, email]],
    city: [null],
    roles: [null, required]
  });

  public profileFormControls = this.profileForm.controls;

  public passwordForm = this.formBuilder.group({
    password: [null, required],
    repeatPassword: [null]
  });

  public passwordFormControls = this.passwordForm.controls;

  public roles: string[] = [];
  public Roles = Object.values(Roles);

  constructor(private userService: UsersService,
              private route: ActivatedRoute,
              public authService: AuthService,
              private formBuilder: FormBuilder,
              private router: Router) { }

  ngOnInit(): void {
    this.route.params
      .subscribe({
        next: routeParam => {
          this.retrieveUser(routeParam['id'] as string)
            .subscribe({
              next: response => {
                let user = response as UserDto;
                this.setValueInProfileForm(user);
              },
              error: err => {
                console.log(err);
              }
            })
        }
      })
  }

  private retrieveUser(id: string) : Observable<UserDto> {
    return this.userService.getUser(id)
      .pipe(
        map(response => {
          return response as UserDto;
        })
      );
  }

  saveProfileData() {
    if(!this.profileForm.valid) {
      return;
    }
    let userDto = this.profileForm.value;
    userDto.id = this.id;
    this.userService.updateUser(userDto)
      .subscribe({
        next: response => {
          let user = response as UserDto;
          this.setValueInProfileForm(user);
        },
        error: err => {
          console.log(err);
        }
      })
  }

  updatePassword() {
    if(!this.passwordForm.valid) {
      return;
    }
    let password = this.passwordFormControls['password'].value;
    this.userService.updatePassword(this.id, password)
      .subscribe({
        next: response => {
          let user = response as UserDto;
          this.setValueInProfileForm(user);
        },
        error: err => {
          console.log(err);
        }
      })
  }

  private setValueInProfileForm(userDto: UserDto) {
    this.id = userDto.id;
    this.roles = userDto.roles;
    this.profileForm.patchValue(userDto);
  }

  public matchPasswords() : boolean {
    let password = this.passwordFormControls['password'].value;
    let repeatPassword = this.passwordFormControls['repeatPassword'].value;
    return password === repeatPassword;
  }

  public matchRole(role: string) :  boolean {
    return this.roles.indexOf(role) != -1;
  }

  delete() {
    this.userService.deleteUser(this.id)
      .subscribe({
        next: () => {
          if(this.authService.isAdmin()) {
            this.router.navigateByUrl(FrontUrls.USERS);
          } else {
            this.authService.logout();
            this.router.navigateByUrl(FrontUrls.MAIN);
          }
        },
        error: err => {
          console.log(err);
        }
      });
  }
}
