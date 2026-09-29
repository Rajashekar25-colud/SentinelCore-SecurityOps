import { useState, useRef, useEffect, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import ProfileAvatar from './ProfileAvatar.jsx';

export default function ProfileDropdown() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);

    const toggleDropdown = useCallback(() => setIsOpen(prev => !prev), []);
    const closeDropdown = useCallback(() => setIsOpen(false), []);

    const handleLogoutClick = useCallback(async () => {
        closeDropdown();
        await logout();
        navigate('/login?logout', { replace: true });
    }, [logout, navigate, closeDropdown]);

    // Click outside listener
    useEffect(() => {
        function handleClickOutside(e) {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
                closeDropdown();
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [closeDropdown]);

    // Escape key listener
    useEffect(() => {
        function handleKeyDown(e) {
            if (e.key === 'Escape' && isOpen) {
                closeDropdown();
            }
        }
        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [isOpen, closeDropdown]);

    // Format display role text
    const displayRole = user?.role ? user?.role.replace('ROLE_', '').replace(/_/g, ' ') : 'OPERATOR';

    // Read mock metadata or save local mock details
    const empId = user?.username === 'admin' ? 'EMP-SC-001' : 'EMP-SC-108';
    const department = user?.username === 'admin' ? 'Security Operations' : 'Threat Response Team';
    const emailVal = user?.username === 'admin' ? 'admin@sentinelcore.com' : `${user?.username || 'operator'}@sentinelcore.com`;
    const fullName = user?.username === 'admin' ? 'John Anderson' : (user?.username || 'Operator Name');

    // Hardcode or retrieve last login formatted
    const lastLoginStr = '25 Jul 2026 • 9:38 PM';

    return (
        <div className="profile-dropdown-container" ref={dropdownRef}>
            <button
                className={`user-profile-menu-btn ${isOpen ? 'open' : ''}`}
                onClick={toggleDropdown}
                aria-haspopup="true"
                aria-expanded={isOpen}
                aria-label="User Profile Menu"
                type="button"
            >
                <ProfileAvatar user={user} size="sm" showStatus={true} />
                <span className="user-profile-username-label">{user?.username || 'Operator'}</span>
                <span className="dropdown-arrow">▼</span>
            </button>

            <div className={`profile-dropdown-menu ${isOpen ? 'open' : ''}`} role="menu">
                <header className="profile-dropdown-header">
                    <ProfileAvatar user={user} size="md" />
                    <div className="profile-detail-info">
                        <h4 className="profile-detail-name">{fullName}</h4>
                        <span className="profile-detail-email">{emailVal}</span>
                        <div className="profile-meta-row">
                            <span className="profile-meta-badge">{displayRole}</span>
                            <span>{empId}</span>
                        </div>
                        <div className="profile-meta-row"><span>{department}</span></div>
                        <div className="profile-status-online"><span className="profile-status-dot" /><span>Online</span></div>
                        <div className="profile-last-login">Last Login: {lastLoginStr}</div>
                    </div>
                </header>

                <section className="profile-dropdown-content">
                    <div className="profile-dropdown-section-title">Account</div>
                    <Link to="/profile?tab=general" className="profile-dropdown-item profile-dropdown-item-featured" onClick={closeDropdown} role="menuitem">
                        <span className="item-icon">✦</span>
                        <span><strong>Edit profile &amp; photo</strong><small>Personal details and avatar</small></span>
                        <span className="item-chevron">›</span>
                    </Link>
                    <Link to="/profile?tab=security" className="profile-dropdown-item" onClick={closeDropdown} role="menuitem">
                        <span className="item-icon">⌁</span>
                        <span><strong>Security</strong><small>Password and account protection</small></span>
                        <span className="item-chevron">›</span>
                    </Link>
                    <Link to="/profile?tab=preferences" className="profile-dropdown-item" onClick={closeDropdown} role="menuitem">
                        <span className="item-icon">◌</span>
                        <span><strong>Preferences</strong><small>Notifications, theme, and language</small></span>
                        <span className="item-chevron">›</span>
                    </Link>
                    <Link to="/profile?tab=support" className="profile-dropdown-item" onClick={closeDropdown} role="menuitem">
                        <span className="item-icon">?</span>
                        <span><strong>Help &amp; support</strong><small>Guidance and feedback</small></span>
                        <span className="item-chevron">›</span>
                    </Link>
                </section>

                <footer className="profile-dropdown-footer">
                    <button type="button" className="profile-dropdown-logout-btn" onClick={handleLogoutClick} role="menuitem">
                        <span>🚪</span> Logout
                    </button>
                </footer>
            </div>
        </div>
    );
}
