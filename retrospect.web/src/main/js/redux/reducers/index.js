import { combineReducers } from "redux";
import session from "./session";
import retrospective from "./retrospective";
import retrospectives from "./retrospectives";

export default combineReducers({ session, retrospective, retrospectives });