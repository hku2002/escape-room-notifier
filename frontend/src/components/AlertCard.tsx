import { FC } from 'react';
// import { UserAlert } from '../types/alert';
import './AlertCard.css';

interface AlertCardProps {
  alert: UserAlert;
}

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
  message?: string | null;
}


const AlertCard: FC<AlertCardProps> = ({ alert }) => {
  const { isActive, theme, dateStart, dateEnd, preferredTimes, numPeople } = alert;

  return (
    <div className={`alert-card ${isActive ? 'active' : 'inactive'}`}>
      <div className="alert-header">
        <span className={`status-badge ${isActive ? 'active' : 'inactive'}`}>
          {isActive ? '활성' : '비활성'}
        </span>
      </div>

      <h3 className="theme-name">{theme.themeName}</h3>
      <p className="cafe-info">
        {theme.cafeName} {theme.branchName}
      </p>

      <div className="alert-details">
        <div className="detail-row">
          <span className="detail-label">기간</span>
          <span className="detail-value">
            {dateStart} ~ {dateEnd}
          </span>
        </div>

        {preferredTimes.length > 0 && (
          <div className="detail-row">
            <span className="detail-label">선호 시간</span>
            <span className="detail-value">
              {preferredTimes.map(t => t.trim()).join(', ')}
            </span>
          </div>
        )}

        <div className="detail-row">
          <span className="detail-label">인원</span>
          <span className="detail-value">{numPeople}명</span>
        </div>
      </div>

      <div className="alert-actions">
        <button className="btn-secondary">상세보기</button>
        <button className="btn-danger">삭제</button>
      </div>
    </div>
  );
};

export default AlertCard;
