export interface Theme {
  themeId: number;
  themeName: string;
  cafeName: string;
  branchName: string;
}

export interface UserAlert {
  alertId: number;
  isActive: boolean;
  theme: Theme;
  dateStart: string;
  dateEnd: string;
  preferredTimes: string[];
  preferredDays: string[];
  numPeople: number;
  notifiedCount: number;
  lastNotifiedAt: string | null;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}
