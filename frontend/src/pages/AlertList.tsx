import { FC, useEffect, useState } from 'react';
import { getUserAlerts } from '../services/api';
import { UserAlert } from '../types/alert';
import AlertCard from '../components/AlertCard';
import './AlertList.css';

const AlertList: FC = () => {
  const [alerts, setAlerts] = useState<UserAlert[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getUserAlerts();
        setAlerts(data);
      } catch (err) {
        setError('알림 목록을 불러오는데 실패했습니다.');
        console.error('Error loading alerts:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAlerts();
  }, []);

  if (loading) {
    return (
      <div className="alert-list-container">
        <div className="loading">로딩 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert-list-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <div className="alert-list-container">
      <header className="header">
        <h1 className="title">방탈출 예약 알림</h1>
        <button className="logout-btn">로그아웃</button>
      </header>

      <main className="main-content">
        <div className="list-header">
          <h2 className="section-title">
            📋 내가 예약한 알림 ({alerts.length})
          </h2>
        </div>

        {alerts.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">📭</div>
            <p className="empty-text">아직 설정된 알림이 없습니다</p>
            <button className="btn-add">+ 새 알림 추가하기</button>
          </div>
        ) : (
          <div className="alerts-grid">
            {alerts.map((alert) => (
              <AlertCard key={alert.alertId} alert={alert} />
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default AlertList;
