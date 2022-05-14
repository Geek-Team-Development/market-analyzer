import {Roles} from "./roles";

export class UserDto {
  id: string | null = null;
  email: string = '';
  password: string = '';
  firstName: string = '';
  lastName: string = '';
  city: string = '';
  roles: Roles[] = [];
}
