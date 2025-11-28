interface Theme {
  themeId: number;
  themeName: string;
  cafeName: string;
  branchName: string;
}

interface UserAlert {
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

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string | null;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const getUserAlerts = async (): Promise<UserAlert[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/user-alerts`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result: ApiResponse<UserAlert[]> = await response.json();

    if (!result.success) {
      throw new Error(result.message || 'Failed to fetch user alerts');
    }

    return result.data;
  } catch (error) {
    console.error('Error fetching user alerts:', error);
    throw error;
  }
};
